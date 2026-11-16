package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.personage.Gardener;
import fr.ubx.poo.ubgarden.game.go.personage.Wasp;
import fr.ubx.poo.ubgarden.game.go.personage.Hornet;
import java.util.*;         //pour List et ArrayList

public class Game {

    private final Configuration configuration;
    private final World world;
    private final Gardener gardener;
    private boolean switchLevelRequested = false;
    private int switchLevel;
    private boolean win = false;

    public Game(World world, Configuration configuration, Position gardenerPosition) {
        this.configuration = configuration;
        this.world = world;
        gardener = new Gardener(this, gardenerPosition);
    }

    public Configuration configuration() {

        return configuration;
    }

    public Gardener getGardener() {
        return this.gardener;
    }

    public World world() {
        return world;
    }

    public boolean isSwitchLevelRequested() {
        return switchLevelRequested;
    }

    public int getSwitchLevel() {
        return switchLevel;
    }

    public boolean allCarrotsCollected(){
        return ((Level) world.getGrid()).allCarrotsCollected();

    }

    public void requestSwitchLevel(int level) {
        this.switchLevel = level;
        switchLevelRequested = true;
    }

    public void clearSwitchLevel() {
        switchLevelRequested = false;
    }

    public List<Wasp> getWasps(){
        return ((Level) world.getGrid()).getWasps();
    }

    public void addWasp(Wasp wasp){
        int lvl = wasp.getPosition().level();
        ((Level) world.getGrid(lvl)).addWasp(wasp);
    }

    public List<Hornet> getHornets(){
        return ((Level) world.getGrid()).getHornets();
    }

    public void addHornet(Hornet hornet){
        int lvl = hornet.getPosition().level();
        ((Level) world.getGrid(lvl)).addHornet(hornet);
    }

    public void requestWin(){
        win = true;
    }

    public boolean isWin(){
        return win;
    }



}
