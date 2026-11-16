package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Level;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.personage.Hornet;


public class NestHornet extends Decor {
    private Timer spawnTimer;           //generation de frelons

    public NestHornet(Game game, Position position){
        super(game, position);
        this.spawnTimer = new Timer(10000); //10sec
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
            Hornet hornet = new Hornet(game, getPosition()); game.addHornet(hornet);      //cree un nvx frelon + ajoutée a la liste des guepes
            
            Level level = (Level) game.world().getGrid();
            level.spawnInsecticide();
            level.spawnInsecticide();   //frelons generent deux bombes
            
            
            
            spawnTimer.start();     //relance le timer pr le prochain frelon
        }
    }
}
