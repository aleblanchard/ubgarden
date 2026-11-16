package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;

public class Flowers extends Decor {
    public Flowers(Position position){
        super(position);
    }

    @Override
    public boolean walkableBy(WalkVisitor gardener){
        return false;
    }
}
