package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Animation;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icrogue.actor.ICRogueActor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;

public abstract class Enemy extends ICRogueActor {
    private boolean isDead;
    private Sprite[][] sprites;
    private Animation[] animations;
    private int currentAnim;
    protected int MOVE_DURATION = 6;

    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public Enemy(Area area, Orientation orientation, DiscreteCoordinates position) {
        super(area, orientation, position);
        isDead = false;
    }

    /**
     * @return (boolean) True si l'ennemi est mort false sinon
     */
    public boolean isDead(){
        return isDead;
    }

    /**
     * Tue l'ennemi
     */
    public void kill(){
        isDead = true;
    }

    /**
     * Setter du sprite de l'ennemi
     * @param sprite Sprite de l'ennemi
     */
    protected void setSprite(Sprite sprite){
        this.sprites = new Sprite[][] {{sprite}};
        animations = new Animation[1];
        animations[0] = new Animation(MOVE_DURATION/2, this.sprites[0]);
        currentAnim = 0;
    }
    protected void setSprites(Sprite[][] sprites){
        this.sprites = sprites;
        animations = Animation.createAnimations(MOVE_DURATION/2, this.sprites, true);
    }
    protected void setSpritesDepth(float depth){
        for (int i = 0; i < sprites.length; i++) {
            for (int j = 0; j < sprites[i].length; j++) {
                sprites[i][j].setDepth(depth);
            }
        }
    }
    @Override
    public void draw(Canvas canvas) {
        animations[currentAnim].draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        currentAnim = this.getOrientation().ordinal();
        if(this.isDisplacementOccurs()){
            animations[currentAnim].update(deltaTime);
        }
        else {
            animations[currentAnim].switchPause();
        }
        super.update(deltaTime);
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
}
