package fr.ubx.poo.ubgarden.game.go.decor;
import fr.ubx.poo.ubgarden.game.Game;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Pickupable;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.Walkable;
import fr.ubx.poo.ubgarden.game.go.bonus.Bonus;

public abstract class Decor extends GameObject implements Walkable, Pickupable {

    private Bonus bonus;

    public Decor(Position position) {
        super(position);
    }
    public Decor(Game game,Position position) {
        super(game,position);
    }
    public Decor(Position position, Bonus bonus) {      //constructeur pour passer une ref vers game (pour les nids)
        super(position);
        this.bonus = bonus;
    }

    public Bonus getBonus() {
        return bonus;
    }

    public void setBonus(Bonus bonus) {
        this.bonus = bonus;
    }

    @Override
    public boolean walkableBy(WalkVisitor gardener) {
        return gardener.canWalkOn(this);
    }

    @Override
    public void update(long now) {
        super.update(now);
        if (bonus != null) bonus.update(now);
    }

}