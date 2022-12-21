package ch.epfl.cs107.play.game.icrogue.hud;


import ch.epfl.cs107.play.game.actor.Entity;
import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;


public class HUD extends Entity {
    private PlayerHpDisplayHUD hpDisplay;
    private KeyIconHUD keyIconHUD;

    /**
     * Default Entity constructor
     */
    public HUD() {
        super(DiscreteCoordinates.ORIGIN.toVector());
        hpDisplay = new PlayerHpDisplayHUD();
        keyIconHUD = new KeyIconHUD();
    }

    public void updateHUD(int playerHP, boolean hasKey){
        hpDisplay.updateHpBar(playerHP);
        keyIconHUD.setDisplayKey(hasKey);
    }

    @Override
    public void draw(Canvas canvas) {
        hpDisplay.draw(canvas);
        keyIconHUD.draw(canvas);
    }
}
