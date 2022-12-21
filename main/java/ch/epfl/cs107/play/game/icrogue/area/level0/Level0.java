package ch.epfl.cs107.play.game.icrogue.area.level0;

import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.game.icrogue.Screens.WinScreen;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.area.Level;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0KeyRoom;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0Room;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0StaffRoom;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0TurretRoom;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level0 extends Level {
    private final int bossKeyId = 1;
    public Level0(ICRogue game) {
        super(true, new DiscreteCoordinates(1,5), RoomType.getDistributionList(), 4,2);
        for (ICRogueRoom[] icRogueRooms : map) {
            for (ICRogueRoom icRogueRoom : icRogueRooms) {
                if (icRogueRoom != null) {
                    game.addArea(icRogueRoom);
                }
            }
        }
    }

    @Override
    protected void generateFixedMap() {
        //generateMap1();
        generateMap2();
    }

    @Override
    protected void createRoom(int roomType, DiscreteCoordinates roomCoordinates) {
        RoomType type = RoomType.values()[roomType];
        switch (type){
            case TURRET_ROOM -> {
                setRoom(roomCoordinates, new Level0TurretRoom(roomCoordinates));
            }
            case STAFF_ROOM -> {
                setRoom(roomCoordinates, new Level0StaffRoom(roomCoordinates));
            }
            case BOSS_KEY -> {
                setRoom(roomCoordinates, new Level0KeyRoom(roomCoordinates, bossKeyId));}
            case SPAWN -> {
                setRoom(roomCoordinates, new Level0Room(roomCoordinates));
                setFirstRoomTitle(roomCoordinates);
            }
            default -> {
                setRoom(roomCoordinates, new Level0Room(roomCoordinates));
            }
        }
    }
    protected ICRogueRoom createBoosRoom(DiscreteCoordinates roomCoordinates) {
        return new Level0TurretRoom(roomCoordinates);
    }

    @Override
    protected void setUpConnector(MapState[][] roomsPlacement, ICRogueRoom room) {
        if(room !=null){
            DiscreteCoordinates currentRoomCoordinates = room.getPosition();
            for (Level0Room.Level0Connectors connector: Level0Room.Level0Connectors.values()) {
                DiscreteCoordinates destination = currentRoomCoordinates.jump(connector.getOrientation().toVector());
                if(destination.x >=0 && destination.x<roomsPlacement.length && destination.y >= 0 && destination.y<roomsPlacement[0].length && roomsPlacement[destination.x][destination.y] != MapState.NULL){
                    String destinationTitle = map[destination.x][destination.y].getTitle();
                    if(roomsPlacement[destination.x][destination.y] == MapState.CREATED){
                        setRoomConnector(currentRoomCoordinates, destinationTitle, connector, Connector.ICRogueConnectorState.CLOSED);
                    }
                    else if(roomsPlacement[destination.x][destination.y] == MapState.BOSS_ROOM){
                        setRoomConnector(currentRoomCoordinates, destinationTitle, connector, bossKeyId);
                    }
                    else {
                        setRoomConnector(currentRoomCoordinates, destinationTitle, connector, Connector.ICRogueConnectorState.INVISIBLE);
                    }
                }
            }
        }
    }

    private void generateMap1(){
        int keyID = 1;

        //création de la salle 00
        DiscreteCoordinates room00 = new DiscreteCoordinates(0,0);
        setRoom(room00, new Level0KeyRoom(room00, keyID));
        //mise en place du connecteur
        setRoomConnector(room00, "icrogue/level010", Level0Room.Level0Connectors.E);
        //Verrouillage de la porte
        lockRoomConnector(room00, Level0Room.Level0Connectors.E, keyID);

        //création de la salle 01
        DiscreteCoordinates room01 = new DiscreteCoordinates(0,1);
        setRoom(room01, new Level0Room(room01));
        setRoomConnector(room01, "icrogue/level000", Level0Room.Level0Connectors.W, Connector.ICRogueConnectorState.OPEN);
    }

    private void generateMap2(){
        int BOSS_KEY_ID = 2;
        DiscreteCoordinates room00 = new DiscreteCoordinates(0, 0);
        setRoom(room00, new Level0TurretRoom(room00));
        setRoomConnector(room00, "icrogue/level001", Level0Room.Level0Connectors.E, Connector.ICRogueConnectorState.CLOSED);

        DiscreteCoordinates room10 = new DiscreteCoordinates(1,0);
        setRoom(room10, new Level0Room(room10));
        setRoomConnector(room10, "icrogue/level011", Level0Room.Level0Connectors.S, Connector.ICRogueConnectorState.CLOSED);
        setRoomConnector(room10, "icrogue/level002", Level0Room.Level0Connectors.E, Connector.ICRogueConnectorState.CLOSED);

        setRoomConnectorDestination(room10, "icrogue/level000", Level0Room.Level0Connectors.W);
        lockRoomConnector(room10, Level0Room.Level0Connectors.W,  BOSS_KEY_ID);

        DiscreteCoordinates room20 = new DiscreteCoordinates(2,0);
        setRoom(room20,  new Level0StaffRoom(room20));
        setRoomConnector(room20, "icrogue/level001", Level0Room.Level0Connectors.W, Connector.ICRogueConnectorState.CLOSED);
        setRoomConnector(room20, "icrogue/level003", Level0Room.Level0Connectors.E, Connector.ICRogueConnectorState.CLOSED);

        DiscreteCoordinates room30 = new DiscreteCoordinates(3,0);
        setRoom(room30, new Level0KeyRoom(room30, BOSS_KEY_ID));
        setRoomConnector(room30, "icrogue/level002", Level0Room.Level0Connectors.W, Connector.ICRogueConnectorState.CLOSED);

        DiscreteCoordinates room11 = new DiscreteCoordinates(1, 1);
        setRoom (room11, new Level0Room(room11));
        setRoomConnector(room11, "icrogue/level001", Level0Room.Level0Connectors.N, Connector.ICRogueConnectorState.CLOSED);

    }

    public enum RoomType{
        TURRET_ROOM(5),
        STAFF_ROOM(1),
        BOSS_KEY(1),
        SPAWN(1),
        NORMAL(1);

        private int nbRoom;
        RoomType(int nbRoom){
            this.nbRoom = nbRoom;
        }
        public static int[] getDistributionList(){
            int[] list = new int[RoomType.values().length];
            for (RoomType type:
                 RoomType.values()) {
                list[type.ordinal()] = type.nbRoom;
            }
            return list;
        }
    }
}
