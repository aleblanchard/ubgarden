    /*
     * Copyright (c) 2020. Laurent Réveillère
     */

    package fr.ubx.poo.ubgarden.game.engine;

    import fr.ubx.poo.ubgarden.game.Direction;
    import fr.ubx.poo.ubgarden.game.Game;
    import fr.ubx.poo.ubgarden.game.Level;
    import fr.ubx.poo.ubgarden.game.Position;
    import fr.ubx.poo.ubgarden.game.go.decor.DoorPrevOpened;
    import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
    import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
    import fr.ubx.poo.ubgarden.game.go.decor.Decor;
    import fr.ubx.poo.ubgarden.game.go.decor.DoorNextClosed;
    import fr.ubx.poo.ubgarden.game.go.decor.DoorNextOpened;
    import fr.ubx.poo.ubgarden.game.view.ImageResource;
    import fr.ubx.poo.ubgarden.game.view.Sprite;
    import fr.ubx.poo.ubgarden.game.view.SpriteFactory;
    import fr.ubx.poo.ubgarden.game.view.SpriteGardener;
    import fr.ubx.poo.ubgarden.game.view.SpriteWasp;
    import javafx.animation.AnimationTimer;
    import javafx.application.Platform;
    import javafx.scene.Group;
    import javafx.scene.Scene;
    import javafx.scene.layout.Pane;
    import javafx.scene.layout.StackPane;
    import javafx.scene.paint.Color;
    import javafx.scene.text.Font;
    import javafx.scene.text.Text;
    import javafx.scene.text.TextAlignment;
    import fr.ubx.poo.ubgarden.game.go.personage.Wasp;
    import fr.ubx.poo.ubgarden.game.go.personage.Hornet;
    import fr.ubx.poo.ubgarden.game.view.SpriteHornet;


    import java.util.*;


    public final class GameEngine {


        private AnimationTimer gameLoop;        //SUPPRESSION DU STATIC, créeait un crash quand on lancait un nouveau jeu sans stopper l'ancien
        private final Game game;
        private final Gardener gardener;
        private final List<Sprite> sprites = new LinkedList<>();
        private final Set<Sprite> cleanUpSprites = new HashSet<>();

        private final Scene scene;

        private StatusBar statusBar;

        private final Pane rootPane = new Pane();
        private final Group root = new Group();
        private final Pane layer = new Pane();
        private Input input;

        private boolean doorOpened = false;

        public GameEngine(Game game, Scene scene) {
            this.game = game;
            this.scene = scene;
            this.gardener = game.getGardener();
            initialize();
            buildAndSetGameLoop();
        }

        public Pane getRoot() {
            return rootPane;
        }

        private void initialize() {
            int height = game.world().getGrid().height();
            int width = game.world().getGrid().width();
            int sceneWidth = width * ImageResource.size;
            int sceneHeight = height * ImageResource.size;
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());
            input = new Input(scene);

            root.getChildren().clear();
            root.getChildren().add(layer);
            statusBar = new StatusBar(root, sceneWidth, sceneHeight);

            rootPane.getChildren().clear();
            rootPane.setPrefSize(sceneWidth, sceneHeight + StatusBar.height);
            rootPane.getChildren().add(root);

            // Create sprites

            for (var decor : game.world().getGrid().values()) {
                sprites.add(SpriteFactory.create(layer, decor));
                decor.setModified(true);
                var bonus = decor.getBonus();
                if (bonus != null) {
                    sprites.add(SpriteFactory.create(layer, bonus));
                    bonus.setModified(true);
                    bonus.setSprite(true);      //note que le bonus a deja un sprite
                }
            }
            for (Wasp wasp : game.getWasps()){      //recree les sprites supprimés des insectes quand on revient sur un jardin deja visité
                if (!wasp.hasCreated()){
                    sprites.add(new SpriteWasp(layer, wasp));
                    wasp.setCreation(true);
                    wasp.setModified(true);         //force le redessin au prochain render
                }
            }
            for (Hornet hornet : game.getHornets()){      
                if (!hornet.hasCreated()){
                    sprites.add(new SpriteHornet(layer, hornet));
                    hornet.setCreation(true);
                    hornet.setModified(true);        
                }
            }

            sprites.add(new SpriteGardener(layer, gardener));
            resizeScene(sceneWidth, sceneHeight);
        }

        void buildAndSetGameLoop() {
            gameLoop = new AnimationTimer() {
                public void handle(long now) {
                    checkLevel();

                    // Check keyboard actions
                    processInput();

                    // Do actions
                    update(now);
                    checkCollision();
                    walkInsecticide();
                    // Graphic update
                    cleanupSprites();
                    render();
                    statusBar.update(game);
                }
            };
        }


        private void checkLevel() {          
            if (game.isSwitchLevelRequested()) {
                // Find the new level to switch to
                int levelToSwitchTo = game.getSwitchLevel();
                // clear all sprites
                sprites.clear();

                // change the current level
                game.world().setCurrentLevel(levelToSwitchTo);
                doorOpened = false;



                for (Wasp wasp : game.getWasps()){      //on attribue created a false pour que initialize leur recrée un sprite
                    wasp.setCreation(false);    //quand on revient sur le niveau
                }                                           //du fait du sprites.clear plus haut
                for (Hornet hornet : game.getHornets()){
                    hornet.setCreation(false);
                }





                // Find the position of the door to reach
                Level newLevel = (Level) game.world().getGrid();
                Position newPos = new Position(levelToSwitchTo,0,0);
                for (int x = 0; x < newLevel.width(); x++) {
                    for (int y = 0; y < newLevel.height(); y++) {
                        Position pos = new Position(levelToSwitchTo, x, y);
                        Decor decor = newLevel.get(pos);
                        if (decor instanceof DoorPrevOpened) {
                            newPos = pos;
                        }
                    }
                }
                // Set the position of the gardener
                gardener.setPosition(newPos);

                game.clearSwitchLevel();            //on met a false, sinon au prochain tick ça va croire qu'il faut encore changer de lvl et refaire tt en boucle
                // initialize();
                initialize();
            }
        }

        private void checkCollision() {
                Position gardenerPos = gardener.getPosition();

                if(!gardener.canBeHurt()){          //rien faire pendant le timer d'invincibilité
                    return;
                }
                for (Wasp wasp : game.getWasps()) {
                    if (wasp.getPosition().equals(gardenerPos)) {   //compare positions
                        if (gardener.getInsecticideCount()>0){  //utilise la bombe automatiquement, tue l'insecte sans retirer d'energie
                            gardener.useInsecticide();
                        }else {
                            gardener.hurt(20); } //-20 energie
                        gardener.dmgTimer();            //cooldown d'invincibilité
                        wasp.remove();  //guepe meurt
                        return;
                    }
                }
               for (Hornet hornet : game.getHornets()) {
                    if (hornet.getPosition().equals(gardenerPos)) {   //compare positions
                        if (gardener.getInsecticideCount()>0){  //utilise la bombe automatiquement, tue l'insecte sans retirer d'energie
                            gardener.useInsecticide();
                        }else {
                            gardener.hurt(30); //-30 energie
                        }   
                        hornet.hit();       //ajout au compteur de piqures
                 
                        gardener.dmgTimer();//cooldown d'invincibilité
                        if (hornet.hitLimit()) hornet.remove();  //frelon meurt
                        return;     //sortie apres collision
                }}
        }

        private void processInput() {
            if (input.isExit()) {
                gameLoop.stop();
                Platform.exit();
                System.exit(0);
            } else if (input.isMoveDown()) {
                gardener.requestMove(Direction.DOWN);
            } else if (input.isMoveLeft()) {
                gardener.requestMove(Direction.LEFT);
            } else if (input.isMoveRight()) {
                gardener.requestMove(Direction.RIGHT);
            } else if (input.isMoveUp()) {
                gardener.requestMove(Direction.UP);
            }
            input.clear();
        }

        private void showMessage(String msg, Color color) {
            Text message = new Text(msg);
            message.setTextAlignment(TextAlignment.CENTER);
            message.setFont(new Font(60));
            message.setFill(color);

            StackPane pane = new StackPane(message);
            pane.setPrefSize(rootPane.getWidth(), rootPane.getHeight());
            rootPane.getChildren().clear();
            rootPane.getChildren().add(pane);

            new AnimationTimer() {
                public void handle(long now) {
                    processInput();
                }
            }.start();
        }

        private void update(long now) {
            game.world().getGrid().values().forEach(decor -> decor.update(now));
            game.getWasps().forEach(wasp -> wasp.update(now));      //maj des guepes vivantes
            game.getHornets().forEach(hornet -> hornet.update(now));      //maj des frelons vivants

            for (Wasp wasp : game.getWasps()){      //cree une guepe pour chaque guepe sans sprite et la marque comme true (deja affichée)
                if (!wasp.hasCreated()){
                    sprites.add(new SpriteWasp(layer, wasp));
                    wasp.setCreation(true);
                }
            }
            for (Hornet hornet : game.getHornets()){      //cree un sprite pour chaque frelons sans sprite et la marque comme true (deja affichée)
                if (!hornet.hasCreated()){
                    sprites.add(new SpriteHornet(layer, hornet));
                    hornet.setCreation(true);
                }
            }
            
            gardener.update(now);

            if (gardener.getEnergy() < 0) {
                gameLoop.stop();
                showMessage("Perdu!", Color.RED);
            }
            if (game.isWin()){
                gameLoop.stop();
                showMessage("Gagné !", Color.GREEN);
            }
            if (game.allCarrotsCollected() && !doorOpened){
                doorOpened = true;
                Level level = (Level) game.world().getGrid();
                for (int x = 0; x < level.width(); x++){
                    for (int y = 0; y < level.height(); y++){
                        Position pos = new Position(game.world().currentLevel(),x,y);
                        Decor decor = level.get(pos);
                        if (decor instanceof DoorNextClosed){
                            decor.remove();
                            DoorNextOpened newDoor = new DoorNextOpened(pos);
                            level.setDecor(pos,newDoor);
                            sprites.add(SpriteFactory.create(layer,newDoor));
                        }
                    }
                }

            }
            for (var decor : game.world().getGrid().values()){      //pour chaque bonus sans sprite, on en crée un (comme pour les guepes)
                var bonus = decor.getBonus();
                if (bonus != null && !bonus.hasSprite()){
                    sprites.add(SpriteFactory.create(layer, bonus));
                    bonus.setModified(true);
                    bonus.setSprite(true);
                }
            }
        }

        public void cleanupSprites() {
            sprites.forEach(sprite -> {
                if (sprite.getGameObject().isDeleted()) {
                    cleanUpSprites.add(sprite);
                }
            });
            cleanUpSprites.forEach(Sprite::remove);
            sprites.removeAll(cleanUpSprites);
            cleanUpSprites.clear();

            game.getWasps().removeIf(wasp -> wasp.isDeleted());     //retire les guepes mortes de la liste de jeu pour éviter de les update encore
            game.getHornets().removeIf(hornet -> hornet.isDeleted());     //retire les frelons morts la liste de jeu pour éviter de les update encore

        }



        private void render() {
            sprites.forEach(Sprite::render);
        }

        public void start() {
            gameLoop.start();
        }

        public void stop(){         //stopper la boucle quand on charge une nouvelle partie quand une ancienne est encore en cours
            if (gameLoop != null){
                gameLoop.stop();
            }
        }

        private void resizeScene(int width, int height) {
            rootPane.setPrefSize(width, height + StatusBar.height);
            layer.setPrefSize(width, height);
            Platform.runLater(() -> scene.getWindow().sizeToScene());
        }


        private void walkInsecticide(){
            for (Hornet hornet : game.getHornets()){
                Decor decor = game.world().getGrid().get(hornet.getPosition());
                var bonus = decor.getBonus();
                if (bonus instanceof Insecticide){      //si c'est une bombe
                    hornet.hit();                   //hit
                    bonus.remove();                 //supprime la bombe
                    if(hornet.hitLimit()) hornet.remove();      //check si le frelon doit mourir ou pas
                }  
            }
            for (Wasp wasp : game.getWasps()){      //meme chose pour les guepes
                Decor decor = game.world().getGrid().get(wasp.getPosition());
                var bonus = decor.getBonus();
                if (bonus instanceof Insecticide){      //si c'est une bombe
                    wasp.remove();             
                    bonus.remove();                 //supprime la bombe
                }  
            }
        
        }


    }