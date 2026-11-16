package fr.ubx.poo.ubgarden.game.launcher;

import fr.ubx.poo.ubgarden.game.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class GameLauncher {

    private GameLauncher() {
    }

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {

        // Load parameters
        int waspMoveFrequency = integerProperty(properties, "waspMoveFrequency", 1);
        int hornetMoveFrequency = integerProperty(properties, "hornetMoveFrequency", 2);

        int gardenerEnergy = integerProperty(properties, "gardenerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        long energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 1_000);
        long diseaseDuration = integerProperty(properties, "diseaseDuration", 5_000);

        return new Configuration(gardenerEnergy, energyBoost, energyRecoverDuration, diseaseDuration, waspMoveFrequency, hornetMoveFrequency);
    }

    public Game load(File file) {
        Properties fileProperties = new Properties();
        try (FileReader reader = new FileReader(file)) {
            fileProperties.load(reader);
        } catch (IOException e) {
            System.out.println(e.getMessage()); //comme vu en CM pour afficher l'erreur
            return null;
        }
        Configuration config = getConfiguration(fileProperties);
        int nblevel = integerProperty(fileProperties, "levels", 1);
        World world = new World(nblevel);
        Game game = null;
        boolean createGame = false;

        for (int i = 1; i <=nblevel; i++) { //pour chaque niveau
            String mapString = fileProperties.getProperty("level" + i);  //prend la ligne "level1"GGGG... "level2" etc dans le fichier
            int width = 0;
            while (width< mapString.length() && mapString.charAt(width)!='x') { //comme dans td la largeur c'est le nombre de caractères avant le premier 'x'
                width++;
            }
            int height = 0;
            for (int j = 0; j< mapString.length();j++) {  //chaque x = hauteur+1 car x retour à la ligne
                if (mapString.charAt(j) == 'x') {
                    height++;
                }
            }

            MapLevel mapLevel = new MapLevel(width,height); //creer la grille
            int x = 0;
            int y = 0;
            for (int j = 0; j< mapString.length(); j++) {
                char c = mapString.charAt(j);
                if (c == 'x') {
                    y++; //retour ligne
                    x = 0;  //revient à gauche
                }else {
                    mapLevel.set(x, y,MapEntity.fromCode(c)); //met sur la grille les objets de chaque niveau present dans MapEntity
                    x++;
                }
            }
            if (!createGame) { //si premier niveau on creer le jeu qui sera utilisé pour tous les autres niveau
                Position pos = mapLevel.getGardenerPosition();
                game = new Game(world,config,pos);
                createGame = true;
            }
            Map currentLevel = new Level(game, i, mapLevel);
            world.put(i, currentLevel);
        }
        return game;
    }

    public Game load() {
        Properties emptyConfig = new Properties();
        MapLevel mapLevel = new MapLevelDefaultStart();
        Position gardenerPosition = mapLevel.getGardenerPosition();
        if (gardenerPosition == null)
            throw new RuntimeException("Gardener not found");
        Configuration configuration = getConfiguration(emptyConfig);
        World world = new World(1);
        Game game = new Game(world, configuration, gardenerPosition);
        Map level = new Level(game, 1, mapLevel);
        world.put(1, level);
        return game;
    }

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }

}
