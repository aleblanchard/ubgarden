package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class DoorNextOpened extends Decor {
    public DoorNextOpened(Position position){
        super(position);
    }

    public void pickUpBy(Gardener gardener){
        gardener.pickUp(this);
    }
    public boolean walkableBy(WalkVisitor v){
        return v.canWalkOn(this);
    }

}
