package fr.ubx.poo.ubgarden.game.go.decor;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.personage.Gardener;

public class Hedgehog extends Decor{
    public Hedgehog(Position position){
        super(position);
    }

    @Override
    public boolean walkableBy(WalkVisitor gardener){
        return true;
    }

    @Override
    public void pickUpBy(Gardener gardener){
        gardener.pickUp((this));
    }
}
