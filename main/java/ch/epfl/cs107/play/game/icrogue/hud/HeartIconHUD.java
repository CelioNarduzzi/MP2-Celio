package ch.epfl.cs107.play.game.icrogue.hud;

import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class HeartIconHUD extends Entity {
    private Sprite[] sprites;
    private int spriteIndex = 2;
    public HeartIconHUD(int index) {
        super(new DiscreteCoordinates(index,9).toVector());
        sprites = Sprite.extractSprites("zelda/heartDisplay", 3, 1,1, this, 16, 16);
        sprites[0].setDepth(Integer.MAX_VALUE);
        sprites[1].setDepth(Integer.MAX_VALUE);
        sprites[2].setDepth(Integer.MAX_VALUE);
    }

    public void setSpriteIndex(int spriteIndex) {
        this.spriteIndex = spriteIndex;
    }

    @Override
    public void draw(Canvas canvas) {
        sprites[spriteIndex].draw(canvas);
    }
}
