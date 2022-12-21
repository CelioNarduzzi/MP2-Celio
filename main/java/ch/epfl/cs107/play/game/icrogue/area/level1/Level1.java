package ch.epfl.cs107.play.game.icrogue.area.level1;

import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.area.Level;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0KeyRoom;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0Room;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0StaffRoom;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0TurretRoom;
import ch.epfl.cs107.play.game.icrogue.area.level1.rooms.Level1CoinRoom;
import ch.epfl.cs107.play.game.icrogue.area.level1.rooms.Level1FlameSkullRoom;
import ch.epfl.cs107.play.game.icrogue.area.level1.rooms.Level1LogRoom;
import ch.epfl.cs107.play.game.icrogue.area.level1.rooms.Level1Room;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level1 extends Level {
    private int bossKeyId = 1;
    public Level1(ICRogue game) {
        super(true, new DiscreteCoordinates(1,5), RoomType.getDistributionList(), 4,2);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
               if(map[i][j]!=null){
                   game.addArea(map[i][j]);
               }
            }
        }
    }


    @Override
    protected void generateFixedMap() {}

    @Override
    protected void createRoom(int roomType, DiscreteCoordinates roomCoordinates) {
        RoomType type = RoomType.values()[roomType];
        System.out.println(type);
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
                setRoom(roomCoordinates, new Level1Room(roomCoordinates));
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
