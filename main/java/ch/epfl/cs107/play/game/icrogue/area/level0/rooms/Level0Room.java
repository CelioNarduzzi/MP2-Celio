package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Log;
import ch.epfl.cs107.play.game.icrogue.area.ConnectorInRoom;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class Level0Room extends ICRogueRoom {
    @Override
    public boolean isOn() {
        return true;
    }

    @Override
    public boolean isOff() {
        return false;
    }

    @Override
    public float getIntensity() {
        return 1;
    }

    public enum Level0Connectors implements ConnectorInRoom {
        W(new DiscreteCoordinates(0,4), new DiscreteCoordinates(8,5), Orientation.LEFT),
        S(new DiscreteCoordinates(4,0), new DiscreteCoordinates(5,8), Orientation.DOWN),
        E(new DiscreteCoordinates(9,4), new DiscreteCoordinates(1, 5), Orientation.RIGHT),
        N(new DiscreteCoordinates(4,9), new DiscreteCoordinates(5,1), Orientation.UP);

        DiscreteCoordinates coordinates;
        DiscreteCoordinates destination;
        Orientation orientation;

        Level0Connectors(DiscreteCoordinates coordinates, DiscreteCoordinates destination, Orientation orientation){
            this.destination = destination;
            this.coordinates = coordinates;
            this.orientation = orientation;
        }
        @Override
        public int getIndex() {
            return this.ordinal();
        }
        @Override
        public DiscreteCoordinates getDestination() {
            return destination;
        }
        public static List<Orientation> getAllConnectorsOrientation(){
            List<Orientation> orientations = new ArrayList<>();
            for(Level0Connectors connector:Level0Connectors.values()){
                orientations.add(connector.orientation);
            }
            return orientations;
        }
        public Orientation getOrientation() {
            return orientation;
        }
        public static List<DiscreteCoordinates> getAllConnectorsPosition(){
            List<DiscreteCoordinates> coordinates = new ArrayList<>();
            for(Level0Connectors connector:Level0Connectors.values()){
                coordinates.add(connector.coordinates);
            }
            return coordinates;
        }
    }

    public Level0Room(DiscreteCoordinates roomCoordinates){
        super(Level0Connectors.getAllConnectorsPosition(), Level0Connectors.getAllConnectorsOrientation(),"icrogue/Level0Room", roomCoordinates);
    }

    @Override
    public String getTitle() {
        return "icrogue/level0" + position.y + position.x;
    }
    
    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return new DiscreteCoordinates(2,10);
    }

    protected void createArea() {
        // Base
        registerActor(new Background(this, behaviorName));
    }

    @Override
    public void update(float deltaTime) {
        if(isOn()){
            for (Connector connector:
                 connectors) {
                connector.openConnector();
            }
        }
        super.update(deltaTime);
    }
}

