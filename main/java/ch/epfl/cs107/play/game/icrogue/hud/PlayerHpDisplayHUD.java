package ch.epfl.cs107.play.game.icrogue.hud;

import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class PlayerHpDisplayHUD extends Entity {
    private HeartIconHUD[] heartsDisplay = new HeartIconHUD[5];
    /**
     * Default Entity constructor
     */
    public PlayerHpDisplayHUD() {
        super(new DiscreteCoordinates(0,9).toVector());
        for (int i = 0; i < 5; i++) {
            heartsDisplay[i] = new HeartIconHUD(i);
        }
    }

    public void updateHpBar(int hp){
        if(hp<0) return;
        int nbFullHeart = hp/2;
        int halfHeart = hp%2;
        for (int i = 0; i < nbFullHeart; i++) {
            heartsDisplay[i].setSpriteIndex(2);
        }
        if(halfHeart == 1){
            heartsDisplay[nbFullHeart].setSpriteIndex(1);
        }
        for(int i = nbFullHeart + halfHeart; i<5; i++){
            heartsDisplay[i].setSpriteIndex(0);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < 5; i++) {
            heartsDisplay[i].draw(canvas);
        }
    }
}
