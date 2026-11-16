package fr.ubx.poo.ubgarden.game.launcher;


public enum MapEntity {
    PoisonedApple('-'),
    Apple('+'),
    Carrots('F'),
    Flowers('O'),
    Grass('G'),
    Land('L'),
    Tree('T'),
    Insecticide('B'),            //bomb
    CarrotsLand('f'),           //carrots sur terre

    Gardener('P'),
    Hedgehog('H'),
    DoorPrevOpened('<'),
    DoorNextOpened('>'),
    DoorNextClosed('D'),

    NestWasp('n'),
    NestHornet('N');


    private final char code;

    MapEntity(char c) {
        this.code = c;
    }

    public static MapEntity fromCode(char c) {
        for (MapEntity mapEntity : values()) {
            if (mapEntity.code == c)
                return mapEntity;
        }
        throw new MapException("Invalid character " + c);
    }

    @Override
    public String toString() {
        return Character.toString(code);
    }

}
