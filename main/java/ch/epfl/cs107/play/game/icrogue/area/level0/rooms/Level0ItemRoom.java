package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.actor.items.Item;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.List;

public abstract class Level0ItemRoom extends Level0Room{
    List<Item> items = new ArrayList<>();

    public Level0ItemRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
    }

    /**
     * @param item Item à ajouter dans la salle
     */
    public void addItem(Item item){
        items.add(item);
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            for (Item item:
                    items) {
                registerActor(item);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isOn() {
        boolean isItemsCollected = true;
        for (Item item:
                items) {
            if(!item.isCollected()){
                isItemsCollected = false;
                break;
            }
        }
        return isItemsCollected && isVisited;

    }

    @Override
    public boolean isOff() {

        return !isOn();
    }
}
