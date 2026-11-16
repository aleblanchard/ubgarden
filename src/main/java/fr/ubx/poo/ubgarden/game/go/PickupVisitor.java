package fr.ubx.poo.ubgarden.game.go;

import fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;
import fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import fr.ubx.poo.ubgarden.game.go.decor.DoorNextClosed;
import fr.ubx.poo.ubgarden.game.go.decor.DoorNextOpened;
import fr.ubx.poo.ubgarden.game.go.decor.DoorPrevOpened;


public interface PickupVisitor {
    /**
     * Called when visiting and picking up an {@link EnergyBoost}.
     *
     * @param energyBoost the energy boost to be picked up
     */
    default void pickUp(EnergyBoost energyBoost) {
    }

    default void pickUp(Hedgehog hedgehog) {
    }

    default void pickUp(PoisonedApple poisonedApple) {
    }

    default void pickUp(Carrots carrots) {
    }

    default void pickUp(Insecticide insecticide){
    }

    default void pickUp(DoorNextOpened d){
    }

    default void pickUp(DoorNextClosed d){
    }

    default void pickUp(DoorPrevOpened d){
    }
}
