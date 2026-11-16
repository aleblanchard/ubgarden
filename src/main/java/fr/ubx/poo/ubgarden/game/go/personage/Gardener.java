/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ubgarden.game.go.personage;

import fr.ubx.poo.ubgarden.game.Direction;
import fr.ubx.poo.ubgarden.game.Game;
import fr.ubx.poo.ubgarden.game.Level;
import fr.ubx.poo.ubgarden.game.Map;
import fr.ubx.poo.ubgarden.game.Position;
import fr.ubx.poo.ubgarden.game.go.GameObject;
import fr.ubx.poo.ubgarden.game.go.Movable;
import fr.ubx.poo.ubgarden.game.go.PickupVisitor;
import fr.ubx.poo.ubgarden.game.go.WalkVisitor;
import fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.decor.Decor;
import fr.ubx.poo.ubgarden.game.engine.Timer;
import fr.ubx.poo.ubgarden.game.go.decor.Hedgehog;
import fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import fr.ubx.poo.ubgarden.game.go.decor.DoorPrevOpened;
import fr.ubx.poo.ubgarden.game.go.decor.DoorNextOpened;
import java.util.*;

public class Gardener extends GameObject implements Movable, PickupVisitor, WalkVisitor {

    private int energy;                                 //energie doit etre modifiée, supp du final
    private final int maxEnergy;
    private int diseaseLevel = 1;
    private int insecticideCount = 0;

    private final Timer energyRecoverTimer;
    private final List<Timer> diseaseTimers = new ArrayList<>();  //liste de timers pour respecter la durée propre de chaque pomme pourrie
    private final Timer dmgCooldownTimer;        //éviter que le frelons mette 2 hits "d'un coup" en étant sur sa case

    private Direction direction;
    private boolean moveRequested = false;


    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.maxEnergy = game.configuration().gardenerEnergy();
        this.energy = maxEnergy;
        this.energyRecoverTimer = new Timer(game.configuration().energyRecoverDuration());
        this.dmgCooldownTimer = new Timer(1000);    //1 sec d'invulnérabilité
    }


    public boolean canBeHurt(){     //true si le gardener peut prendre des degats
        return !dmgCooldownTimer.isRunning();
    }

    public void dmgTimer(){         //appel apres un hit pour activer le cooldown
        dmgCooldownTimer.start();
    }

    @Override
    public void pickUp(EnergyBoost energyBoost) {
        energy = Math.min(energy + game.configuration().energyBoost(), maxEnergy);
        diseaseTimers.clear();              //la pomme guerit des maladies actives
        diseaseLevel = 1;
        energyBoost.remove();
    }

    @Override
    public void pickUp(Carrots carrots) {
        int lvl = carrots.getPosition().level();
        Level level = (Level)game.world().getGrid(lvl);
        if (level != null) level.collectCarrot();
        carrots.remove();
    }

    @Override
    public void pickUp(DoorNextOpened door) {
        int t = door.getPosition().level() +1;  //on verifie que le niveau existe avant de demander, sinon ça plante (renvoie null)
        if (game.world().getGrid(t) != null){
            game.requestSwitchLevel(t);
        }
    }

    @Override
    public void pickUp(DoorPrevOpened door) {
        int t = door.getPosition().level() -1;  //on verifie que le niveau existe avant de demander, sinon ça plante (renvoie null)
        if (game.world().getGrid(t) != null){
            game.requestSwitchLevel(t);
        }
    }

    @Override
    public void pickUp(PoisonedApple poisonedApple) {
            Timer t = new Timer(game.configuration().diseaseDuration()); //nvx timer pour cette poisonnedApple
            t.start();
            diseaseTimers.add(t);               //ajoute a la liste des poisonnedApples actives
            diseaseLevel = 1 + diseaseTimers.size();
        poisonedApple.remove();  //supprime la poisonnedApple apres etre passé dessus
    }

    @Override
    public void pickUp(Hedgehog hedgehog) {
        game.requestWin();
    }

    @Override
    public void pickUp(Insecticide insecticide) {
        insecticideCount++;
        insecticide.remove();
    }

    public int getEnergy() {
        return this.energy;
    }

    public int getDiseaseLevel(){
        return diseaseLevel;
    }

    public int getInsecticideCount(){
        return insecticideCount;
    }

    public Direction getDirection(){
        return direction;
    }


    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }

    @Override
    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Map zone = game.world().getGrid();
        if (!zone.inside(nextPos)) return false;
        Decor next = zone.get(nextPos); 
        if (next == null) return false;
        if (energy < next.energyConsumptionWalk() * diseaseLevel){
            return false;
        }
        return next.walkableBy(this);

    }

    @Override
    public Position move(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        setPosition(nextPos);
        if (next != null) {
            energy -= next.energyConsumptionWalk() * diseaseLevel;
            next.pickUpBy(this);}

        return nextPos;
    }

    public void update(long now) {
        diseaseTimers.forEach(timer -> timer.update(now));      //maj de tous les timers de trognons, on retire ceux qui finissent
        diseaseTimers.removeIf(timer -> !timer.isRunning());
        diseaseLevel = 1 + diseaseTimers.size();


        dmgCooldownTimer.update(now);       //maj du timer d'invincibilité
        energyRecoverTimer.update(now);
        if (moveRequested) {
            if (canMove(direction)) {
                move(direction);
                energyRecoverTimer.start();     //repart a zero quand on bouge
            }}
            else {
                if (!energyRecoverTimer.isRunning()){
                    if (energy < maxEnergy) energy ++;
                    energyRecoverTimer.start();
                }

                }
        moveRequested = false;
    }

    public void hurt(int damage) {
        energy -= damage;
    }

    public void useInsecticide(){
            insecticideCount --;
    }


    @Override
    public boolean canWalkOn(DoorNextOpened door){
        return true;
    }
    @Override
    public boolean canWalkOn(DoorPrevOpened door){
        return true;
    }




}
