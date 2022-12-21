package ch.epfl.cs107.play.game.icrogue.area.level1.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.actor.items.Coin;
import ch.epfl.cs107.play.game.icrogue.area.level0.rooms.Level0ItemRoom;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level1CoinRoom extends Level0ItemRoom {

    public Level1CoinRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
        addItem(new Coin(this, Orientation.UP, new DiscreteCoordinates(5,5), 1));
    }
}
