package ch.epfl.cs107.play.game.icrogue.area;

import ch.epfl.cs107.play.game.icrogue.RandomHelper;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public abstract class Level implements Logic {
    protected ICRogueRoom[][] map;
    protected DiscreteCoordinates startPosition;
    protected DiscreteCoordinates bossRoom;
    protected String firstRoomTitle;

    /**
     * @param startPosition Coordonnées de départ dans les salles
     * @param width Largeur de la carte
     * @param height Hauteur de la salle
     */
    public Level(boolean randomMap, DiscreteCoordinates startPosition, int[] roomsDistribution, int width, int height){
        this.startPosition = startPosition;
        if(!randomMap){
            this.map = new ICRogueRoom[width][height];
            this.bossRoom = new DiscreteCoordinates(0, 0);
            generateFixedMap();
        }
        else{
            generateRandomMap(roomsDistribution);
        }
    }
    public Level(DiscreteCoordinates startPosition, int width, int height){
        this(false, startPosition, null, width, height);
    }

    /**
     * @param coords Position de la salle
     * @param room Salle
     */
    protected void setRoom(DiscreteCoordinates coords, ICRogueRoom room){
        this.map[coords.x][coords.y] = room;
    }

    /**
     * @param coords Position de la salle
     * @param destination Nom de la salle d'arrivée
     * @param connector Connecteur à modifier
     */
    protected  void setRoomConnectorDestination(DiscreteCoordinates coords, String destination, ConnectorInRoom connector){
        map[coords.x][coords.y].connectors.get(connector.getIndex()).setDestination(destination);
        map[coords.x][coords.y].connectors.get(connector.getIndex()).setArrivalCoordinates(connector.getDestination());
    }

    /**
     * @param coords Position de la salle
     * @param destination Nom de la salle d'arrivée
     * @param connector Connecteur à modifier
     */
    protected void setRoomConnector(DiscreteCoordinates coords, String destination, ConnectorInRoom connector){
        setRoomConnector(coords, destination, connector, Connector.ICRogueConnectorState.INVISIBLE);
    }

    /**
     * @param coords Position de la salle
     * @param destination Non de la salle d'arrivée
     * @param connector Connecteur à modifier
     * @param state état du connecteur (INVISIBLE, OPEN, CLOSE)
     */
    protected void setRoomConnector(DiscreteCoordinates coords, String destination, ConnectorInRoom connector, Connector.ICRogueConnectorState state){
        setRoomConnectorDestination(coords, destination, connector);
        map[coords.x][coords.y].connectors.get(connector.getIndex()).closeConnector();
        map[coords.x][coords.y].connectors.get(connector.getIndex()).setState(state);
    }

    /**
     * Crée un connecteur verouillé
     * @param coords Position de la salle
     * @param destination Non de la salle d'arrivée
     * @param connector Connecteur à modifier
     * @param keyID (int) identifiant de la clé
     */
    protected void setRoomConnector(DiscreteCoordinates coords, String destination, ConnectorInRoom connector, int keyID){
        setRoomConnector(coords, destination, connector, Connector.ICRogueConnectorState.LOCKED);
        lockRoomConnector(coords, connector, keyID);
    }

    /**
     * @param coords Position de la salle
     * @param connector Connecteur à modifier
     * @param keyId Identifiant de la clé
     */
    protected void lockRoomConnector(DiscreteCoordinates coords, ConnectorInRoom connector, int keyId){
        map[coords.x][coords.y].connectors.get(connector.getIndex()).lockConnector(keyId);
    }

    /**
     * @param coords Position de la salle de départ
     */
    public void setFirstRoomTitle(DiscreteCoordinates coords) {
        this.firstRoomTitle = map[coords.x][coords.y].getTitle();
    }

    public String getFirstRoomTitle() {
        return firstRoomTitle;
    }

    protected abstract void generateFixedMap();

    //GENERATION ALEATOIRE DES NIVEAUX
    protected void generateRandomMap(int[] roomsDistribution){
        int nbRooms = 0;
        for (int i:
             roomsDistribution) {
            nbRooms += i;
        }
        this.map = new ICRogueRoom[nbRooms][nbRooms];
        MapState[][] roomsPlacement = generateRandomRoomPlacement();
        createRooms(roomsPlacement, roomsDistribution);
        printMap(roomsPlacement);
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                setUpConnector(roomsPlacement, map[i][j]);
            }

        }
    }
    protected MapState[][] generateRandomRoomPlacement(){
        MapState[][] roomsPlacement = new MapState[map.length][map.length];
        for (int i = 0; i < roomsPlacement.length; i++) {
            for (int j = 0; j < roomsPlacement.length; j++) {
                roomsPlacement[i][j] = MapState.NULL;
            }
        }
        ArrayList<DiscreteCoordinates> placedRooms = new ArrayList<>();
        roomsPlacement[map.length/2][map.length/2] = MapState.PLACED;
        placedRooms.add(new DiscreteCoordinates(map.length/2, map.length/2));
        int roomsToPlace = map.length - 1;
        while (roomsToPlace > 0){
            int nbPlacedRooms = placedRooms.size();
            for (int i = 0; i<nbPlacedRooms; i++) {
                if(roomsToPlace == 0) break;
                DiscreteCoordinates currentPlacedRoom = placedRooms.get(i);
                if(roomsPlacement[currentPlacedRoom.x][currentPlacedRoom.y] == MapState.EXPLORED) {
                    continue;
                }
                //compte le nombre de free slot
                int nbFreeSlots = 0;
                ArrayList<DiscreteCoordinates> freeSlots = new ArrayList<>();
                if(currentPlacedRoom.y - 1 >= 0
                        && roomsPlacement[currentPlacedRoom.x][currentPlacedRoom.y - 1] == MapState.NULL){
                    nbFreeSlots += 1;
                    freeSlots.add(new DiscreteCoordinates(currentPlacedRoom.x, currentPlacedRoom.y-1));
                }
                if(currentPlacedRoom.y + 1 < map.length
                        && roomsPlacement[currentPlacedRoom.x][currentPlacedRoom.y + 1] == MapState.NULL){
                    nbFreeSlots += 1;
                    freeSlots.add(new DiscreteCoordinates(currentPlacedRoom.x, currentPlacedRoom.y + 1));
                }
                if(currentPlacedRoom.x + 1 < map.length
                        && roomsPlacement[currentPlacedRoom.x + 1][currentPlacedRoom.y] == MapState.NULL){
                    nbFreeSlots += 1;
                    freeSlots.add(new DiscreteCoordinates(currentPlacedRoom.x + 1, currentPlacedRoom.y));
                }
                if(currentPlacedRoom.x - 1 >= 0
                        && roomsPlacement[currentPlacedRoom.x - 1][currentPlacedRoom.y] == MapState.NULL){
                    nbFreeSlots += 1;
                    freeSlots.add(new DiscreteCoordinates(currentPlacedRoom.x - 1, currentPlacedRoom.y));
                }
                if(nbFreeSlots == 0){continue;}
                int roomsToPlaceNow = RandomHelper.roomGenerator.nextInt(0, Integer.min(roomsToPlace, nbFreeSlots));
                if (roomsToPlaceNow == 0) roomsToPlaceNow++;
                List<Integer> roomsIndexesList = IntStream.rangeClosed(0, freeSlots.size()-1).boxed().toList();
                List<Integer> roomsChosenPosition =  RandomHelper.chooseKInList(roomsToPlaceNow,roomsIndexesList);
                for (Integer posIndex:
                     roomsChosenPosition) {
                    DiscreteCoordinates pos = freeSlots.get(posIndex);
                    roomsPlacement[pos.x][pos.y] = MapState.PLACED;
                    placedRooms.add(pos);
                }
                roomsPlacement[currentPlacedRoom.x][currentPlacedRoom.y] = MapState.EXPLORED;
                roomsToPlace -= roomsToPlaceNow;
            }
        }
        boolean isBoosRoomPlaced = false;
        int i = 0;
        while (!isBoosRoomPlaced && i<placedRooms.size()){
            DiscreteCoordinates currentPlacedRoom = placedRooms.get(i);
            int nbFreeSlots = 0;
            ArrayList<DiscreteCoordinates> freeSlots = new ArrayList<>();

            if(currentPlacedRoom.y - 1 >= 0
                    && roomsPlacement[currentPlacedRoom.x][currentPlacedRoom.y - 1] == MapState.NULL){
                nbFreeSlots += 1;
                freeSlots.add(new DiscreteCoordinates(currentPlacedRoom.x, currentPlacedRoom.y-1));
            }
            if(currentPlacedRoom.y + 1 < map.length
                    && roomsPlacement[currentPlacedRoom.x][currentPlacedRoom.y + 1] == MapState.NULL){
                nbFreeSlots += 1;
                freeSlots.add(new DiscreteCoordinates(currentPlacedRoom.x, currentPlacedRoom.y + 1));
            }
            if(currentPlacedRoom.x + 1 < map.length
                    && roomsPlacement[currentPlacedRoom.x + 1][currentPlacedRoom.y] == MapState.NULL){
                nbFreeSlots += 1;
                freeSlots.add(new DiscreteCoordinates(currentPlacedRoom.x + 1, currentPlacedRoom.y));
            }
            if(currentPlacedRoom.x - 1 >= 0
                    && roomsPlacement[currentPlacedRoom.x - 1][currentPlacedRoom.y] == MapState.NULL){
                nbFreeSlots += 1;
                freeSlots.add(new DiscreteCoordinates(currentPlacedRoom.x - 1, currentPlacedRoom.y));
            }
            if(nbFreeSlots == 0){i++; continue;}

            List<Integer> roomsIndexesList = IntStream.rangeClosed(0, freeSlots.size()-1).boxed().toList();
            List<Integer> roomsChosenPosition =  RandomHelper.chooseKInList(1,roomsIndexesList);
            DiscreteCoordinates pos = freeSlots.get(roomsChosenPosition.get(0));
            roomsPlacement[pos.x][pos.y] = MapState.BOSS_ROOM;
            placedRooms.add(pos);
            isBoosRoomPlaced = true;
        }

        return roomsPlacement;
    }
    private void createRooms(MapState[][] roomsPlacement, int[] roomDistribution){
        ArrayList<DiscreteCoordinates> usableLocations = new ArrayList<>();
        for (int i = 0; i < roomsPlacement.length; i++) {
            for (int j = 0; j < roomsPlacement[i].length; j++) {
                if (roomsPlacement[i][j] == MapState.EXPLORED || roomsPlacement[i][j] == MapState.PLACED){
                    usableLocations.add(new DiscreteCoordinates(i,j));
                }
                else if (roomsPlacement[i][j] == MapState.BOSS_ROOM){
                    map[i][j] = createBoosRoom(new DiscreteCoordinates(i,j));
                    this.bossRoom = new DiscreteCoordinates(i,j);
                }
            }
        }
        for(int i = 0; i<roomDistribution.length; i++){
            int k = roomDistribution[i];
            List<Integer> usableLocationsIndexesList = IntStream.rangeClosed(0, usableLocations.size()-1).boxed().toList();
            List<Integer> chosenLocationIndexes = RandomHelper.chooseKInList(k, usableLocationsIndexesList);
            List<DiscreteCoordinates> usedLocation = new ArrayList<>();
            for (int j = 0; j < chosenLocationIndexes.size(); j++) {
                DiscreteCoordinates pos = usableLocations.get(chosenLocationIndexes.get(j));
                usedLocation.add(pos);
                createRoom(i, pos);
                roomsPlacement[pos.x][pos.y] = MapState.CREATED;
            }
            for (int j = 0; j<usedLocation.size(); j++){
                usableLocations.remove(usedLocation.get(j));
            }
        }
    }
    protected abstract void createRoom(int roomType, DiscreteCoordinates roomCoordinates);
    protected abstract ICRogueRoom createBoosRoom(DiscreteCoordinates roomCoordinates);
    protected abstract void setUpConnector(MapState[][] roomsPlacement, ICRogueRoom room);

    private void printMap(MapState[][] map) {
        System.out.println("Generated map:");
        System.out.print(" | ");
        for (int j = 0; j < map[0].length; j++) {
            System.out.print(j + " "); }
        System.out.println(); System.out.print("--|-");
        for (int j = 0; j < map[0].length; j++) {
            System.out.print("--"); }
        System.out.println();
        for (int i = 0; i < map.length; i++) { System.out.print(i + " | ");
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j] + " "); }
            System.out.println(); }
        System.out.println(); }
    @Override
    public boolean isOn() {
        return (map[bossRoom.x][bossRoom.y]!=null&&map[bossRoom.x][bossRoom.y].isOn());
    }

    @Override
    public boolean isOff() {
        return !isOn();
    }

    @Override
    public float getIntensity() {
        return 0;
    }


    protected enum MapState {
        NULL, // Empty space
        PLACED, // The room has been placed but not yet explored by the room placement algorithm
        EXPLORED, // The room has been placed and explored by the algorithm
        BOSS_ROOM, // The room is a boss room
        CREATED; // The room has been instantiated in the room map
        @Override
        public String toString() {
            return Integer.toString(ordinal());
        }
    }

}