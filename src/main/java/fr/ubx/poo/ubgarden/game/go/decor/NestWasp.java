package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.personage.Wasp;
import fr.ubx.poo.ubgarden.game.Level;

public class NestWasp extends Decor {
    private Timer spawnTimer;           //generation de guepe

    public NestWasp(Game game, Position position){
        super(game, position);
        this.spawnTimer = new Timer(5000); //5sec
        this.spawnTimer.start();
    }

    @Override
    public boolean walkableBy(WalkVisitor gardener){
        return gardener.canWalkOn(this);
    }

    @Override
    public void update(long now){
        super.update(now); spawnTimer.update(now);  //on fait avancer le timer
        if (!spawnTimer.isRunning()){
            Wasp wasp = new Wasp(game, getPosition()); game.addWasp(wasp);      //cree une nouvelle guepe + ajoutée a la liste des guepes
            
            Level level = (Level) game.world().getGrid();   //pose aussi une bombe a un endroit random
            level.spawnInsecticide();

            spawnTimer.start();     //relance le timer pr la prochaine guepe (et bombe)
        }
    }
}
