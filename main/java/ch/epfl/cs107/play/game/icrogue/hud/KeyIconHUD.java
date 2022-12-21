package ch.epfl.cs107.play.game.icrogue.hud;

import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class KeyIconHUD extends Entity {
    private Sprite sprite;
    private boolean displayKey = false;
    public KeyIconHUD() {
        super(new DiscreteCoordinates(9,9).toVector());
        sprite = new Sprite("icrogue/key", 1, 1, this);
        sprite.setDepth(Integer.MAX_VALUE);
    }

    public void setDisplayKey(boolean hasKey){
        displayKey = hasKey;
    }
    @Override
    public void draw(Canvas canvas) {
        if(displayKey){
            sprite.draw(canvas);
        }
    }

}
