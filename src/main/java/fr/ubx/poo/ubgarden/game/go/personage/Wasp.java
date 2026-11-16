package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Map;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.decor.*;


public class Wasp extends GameObject implements Movable, WalkVisitor {

    private Direction direction;
    private final Timer moveTimer;


    public Wasp(Game game, Position position){
        super(game, position);
        this.direction = Direction.DOWN;
        int timing = 1000/ (game.configuration().waspMoveFrequency());
        this.moveTimer = new Timer(timing);
        this.moveTimer.start();
    }

    public Direction getDirection(){
        return direction;

    }
    public boolean created = false;
    public boolean hasCreated(){
        return created;
    }
    public void setCreation(boolean created){
        this.created = created;
    }

    @Override
    public boolean canWalkOn(Flowers flowers){
        return true;
    }
    @Override
    public boolean canWalkOn(NestWasp t){
        return true;
    }
    @Override
    public boolean canWalkOn(NestHornet t){
        return true;
    }

    @Override
    public boolean canMove(Direction direction){
        Position nextPos = direction.nextPosition(getPosition());       //mele logique que gardener
        Map zone = game.world().getGrid();
        if (!zone.inside(nextPos)) return false;
        Decor next = zone.get(nextPos); 
        if (next == null) return false;
        return next.walkableBy(this);

    }

    @Override
    public Position move(Direction direction){                      //juste maj de la position
        Position nextPos = direction.nextPosition(getPosition());
        this.direction = direction; setPosition(nextPos);
        return nextPos;
    }


    @Override
    public void update(long now){
        moveTimer.update(now);
        if (!moveTimer.isRunning()){
            Direction random = Direction.random();          //direction au hasard, si impossible -> attends prochain tick
            if (canMove(random)){
                move(random);
            }
            moveTimer.start();      //relance le timer pour le prochain mouvement
        }
    }
}
