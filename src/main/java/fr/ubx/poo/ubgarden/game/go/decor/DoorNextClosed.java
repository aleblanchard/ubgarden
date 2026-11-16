package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;

public class DoorNextClosed extends Decor {
    public DoorNextClosed(Position position){
        super(position);
    }

    public boolean walkableBy(WalkVisitor gardener){
        return false;
    }

}
