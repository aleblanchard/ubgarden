package fr.ubx.poo.ubgarden.game;

import fr.ubx.poo.ubgarden.game.go.bonus.Carrots;
import fr.ubx.poo.ubgarden.game.go.bonus.EnergyBoost;
import fr.ubx.poo.ubgarden.game.go.bonus.Insecticide;
import fr.ubx.poo.ubgarden.game.go.bonus.PoisonedApple;
import fr.ubx.poo.ubgarden.game.go.decor.*;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Grass;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Land;
import fr.ubx.poo.ubgarden.game.launcher.MapEntity;
import fr.ubx.poo.ubgarden.game.launcher.MapLevel;
import fr.ubx.poo.ubgarden.game.go.decor.ground.Ground;

import fr.ubx.poo.ubgarden.game.go.personage.Hornet;
import fr.ubx.poo.ubgarden.game.go.personage.Wasp;

import java.util.Random;        //pour les bombes
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;


public class Level implements Map {

    private final int level;
    private final int width;
    private int totalCarrots = 0;          //   compteur PAR lvl
    private int collectedCarrots = 0;       //ramasses PAR niveaux
    private final int height;
    private final List<Wasp> wasps = new ArrayList<>();               //chaque lvl garde ses propres insectes
    private final List<Hornet> hornets = new ArrayList<>();           //simplifie le changement de niveaux pour les insectes


    private final java.util.Map<Position, Decor> decors = new HashMap<>();
    private Random random = new Random();           //generateur pour less bombes insecticides
    public Level(Game game, int level, MapLevel entities) {
        this.level = level;
        this.width = entities.width();
        this.height = entities.height();

        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++) {
                Position position = new Position(level, i, j);
                MapEntity mapEntity = entities.get(i, j);
                switch (mapEntity) {
                    case Grass:
                        decors.put(position, new Grass(position));
                        break;
                    case Land:
                        decors.put(position, new Land(position));
                        break;
                    case Flowers:
                        decors.put(position, new Flowers(position));
                        break;
                    case Tree:
                        decors.put(position, new Tree(position));
                        break;
                    case Hedgehog:
                        decors.put(position, new Hedgehog(position));
                        break;
                    case DoorNextClosed:
                        decors.put(position, new DoorNextClosed(position));
                        break;
                    case DoorNextOpened:
                        decors.put(position, new DoorNextOpened(position));
                        break;
                    case DoorPrevOpened:
                        decors.put(position, new DoorPrevOpened(position));
                        break;
                    case NestHornet:
                        decors.put(position, new NestHornet(game,position));
                        break;
                    case NestWasp:
                        decors.put(position, new NestWasp(game, position));
                        break;
                    case Carrots: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new Carrots(position, grass));
                        decors.put(position, grass);
                        totalCarrots++;
                        break;
                    }
                    case CarrotsLand: {
                        Decor land = new Land(position);
                        land.setBonus(new Carrots(position, land));
                        decors.put(position, land);
                        totalCarrots++;
                        break;
                    }
                    case Apple: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new EnergyBoost(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    case PoisonedApple: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new PoisonedApple(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    case Insecticide: {
                        Decor grass = new Grass(position);
                        grass.setBonus(new Insecticide(position, grass));
                        decors.put(position, grass);
                        break;
                    }
                    default:
                        throw new RuntimeException("EntityCode " + mapEntity.name() + " not processed");
                }
            }
    }

    @Override
    public int width() {
        return this.width;
    }

    @Override
    public int height() {
        return this.height;
    }

    public List<Wasp> getWasps(){
        return wasps;
    }

    public List<Hornet> getHornets(){
        return hornets;
    }

    public void addWasp(Wasp wasp){
        wasps.add(wasp);
    }

    public void addHornet(Hornet hornet){
        hornets.add(hornet);
    }

    public Decor get(Position position) {
        return decors.get(position);
    }

    public Collection<Decor> values() {
        return decors.values();
    }

    public int getTotalCarrots(){
        return totalCarrots;
    }
    
    public void collectCarrot(){
        collectedCarrots++;
    }

    public boolean allCarrotsCollected(){                   //true si tt les carrotes du lvl ramasses -> ouvre porte
        return collectedCarrots>= totalCarrots;
    }


    @Override
    public boolean inside(Position position) {
        return position.level() == level && position.x() >= 0 && position.x() <width && position.y() >= 0 && position.y() < height;
    }

    public void setDecor(Position position, Decor decor){  //permet de changer à une position le décor d'un niveau
        decors.put(position,decor);
    }


    public void spawnInsecticide(){
        for(int i = 0; i<99; i++){                      //nb de tentatives de trouver une case valide
            int x = random.nextInt(width); int y = random.nextInt(height);
            Position pos = new Position(level,x,y);
            Decor decor = decors.get(pos);

            if (decor instanceof Ground && decor.getBonus() == null){ //la case doit etre du sol et sans bonus
                decor.setBonus(new Insecticide(pos, decor));
                decor.setModified(true); return;
            }
        }
    }


}
