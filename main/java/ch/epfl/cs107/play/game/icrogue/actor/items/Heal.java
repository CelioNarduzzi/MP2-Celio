package ch.epfl.cs107.play.game.icrogue.actor.items;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public abstract class Heal extends Item {
    private int hpGiven;
    public Heal(Area area, Orientation orientation, DiscreteCoordinates position, int hp) {
        super(area, orientation, position);
        hpGiven = hp;
    }

    public int getHpGiven() {
        return hpGiven;
    }
}
