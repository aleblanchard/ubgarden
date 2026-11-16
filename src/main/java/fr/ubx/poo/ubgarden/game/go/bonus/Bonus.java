/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.bonus;

import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Pickupable;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;

public abstract class Bonus extends GameObject implements Pickupable {

    private final Decor decor;

    private boolean spriteCreated = false;

    public Bonus(Position position, Decor decor) {
        super(position);
        this.decor = decor;
    }

    @Override
    public void remove() {
        super.remove();
        decor.setBonus(null);
    }

    public boolean hasSprite(){
        return spriteCreated;
    }
    public void setSprite(boolean spriteCreated){
        this.spriteCreated = spriteCreated;
    }

}
