package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.actor.TextGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.*;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.actor.items.*;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.Fire;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import javax.sound.sampled.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class ICRoguePlayer extends ICRogueActor implements Interactor{
    private int hp;
    Clip clip2;
    private int money = 0;
    private int maxHP = 10;
    private boolean canUseFire;
    protected boolean isChangingRoom;
    private Sprite[][] sprites;
    private Animation[] animations;

    /// Animation duration in frame number

    public ICRoguePlayerInteractionHandler handler;
    private String destinationRoom;
    private DiscreteCoordinates destinationCoordinates;
    private final static int MOVE_DURATION = 6;

    private ArrayList<Integer> keys = new ArrayList<>();
    /**
     * Demo actor
     *
     */
    public ICRoguePlayer(Area owner, Orientation orientation, DiscreteCoordinates coordinates, String spriteName) {
        super(owner, orientation, coordinates);
        this.hp = 10;
        sprites = Sprite.extractSprites("zelda/player", 4, 1, 2,this, 16, 32, new Orientation[] {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT});
        animations = Animation.createAnimations(MOVE_DURATION/2, sprites);
        handler = new ICRoguePlayerInteractionHandler();
        canUseFire = true;
        resetMotion();
    }

    /**
     * Center the camera on the player
     */
    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
    }

    @Override
    public void update(float deltaTime) {
        isChangingRoom = false;
        Keyboard keyboard= getOwnerArea().getKeyboard();

        moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
        moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP));
        moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
        moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
        if(isDisplacementOccurs()) {


            animations[getOrientation().ordinal()].update(deltaTime);
            ICRogue.playSE(5);




        }
        else {
            animations[getOrientation().ordinal()].reset();
        }
        if(keyboard.get(Keyboard.X).isPressed() && canUseFire){
            getOwnerArea().registerActor(new Fire(getOwnerArea(), getOrientation(), getCurrentMainCellCoordinates()));
            ICRogue.playSE(4);

        }
        super.update(deltaTime);

    }
    /**
     * Orientate and Move this player in the given orientation if the given button is down
     * @param orientation (Orientation): given orientation, not null
     * @param b (Button): button corresponding to the given orientation, not null
     */
    private void moveIfPressed(Orientation orientation, Button b){
        if(b.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orientation);
                move(MOVE_DURATION);
            }
        }
    }

    /**
     * Leave an area by unregister this player
     */
    public void leaveArea(){
        getOwnerArea().unregisterActor(this);
    }

    /**
     * Indique si le joueur est en train de changer de salle
     * @return (boolean) : True si le joueur change de salle, false sinon
     */
    public boolean isChangingRoom() {
        return isChangingRoom;
    }

    /**
     * @param area (Area): initial area, not null
     * @param position (DiscreteCoordinates): initial position, not null
     */
    public void enterArea(Area area, DiscreteCoordinates position){
        ((ICRogueRoom)area).setVisited();
        area.registerActor(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
        resetMotion();
    }

    /**
     * Inflige un certain nombre de points de dommage au joueur
     * @param damage (float) : nombre de points de dommage infligé
     */
    public void takeDamages(float damage){
        this.hp -= damage;
    }

    /**
     * Getter du nombre de points de vie
     * @return (float) : nombre de points de vie restant au joueur
     */
    public float getHp(){
            return hp;
    }
    @Override
    public void draw(Canvas canvas) {
        animations[getOrientation().ordinal()].draw(canvas);
    }

    @Override
    public boolean takeCellSpace() {
        return true;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList (getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }
    public void heal(int hpGiven){
        hp += hpGiven;
        if(hp>maxHP){
            hp = maxHP;
        }
    }

    public boolean hasKey(){
        return this.keys.size() > 0;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        return keyboard.get(Keyboard.W).isDown();
    }

    /**
     * Getter du nom de la salle d'arrivée
     * @return (String) : Nom de la salle dans laquelle le joueur essaie d'aller
     */
    public String getDestinationRoom(){
        return destinationRoom;
    }

    /**
     * Getter des coordonnées dans la salle d'arrivée
     * @return (DiscreteCoordinates) : Coordonnées d'arrivée dans la salle dans laquelle le joueur essaie d'aller
     */
    public DiscreteCoordinates getArrivalCoordinate(){
        return destinationCoordinates;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler) v).interactWith(this, isCellInteraction);
    }

    private class ICRoguePlayerInteractionHandler implements ICRogueInteractionHandler{
        @Override
        public void interactWith(Heal other, boolean isCellInteraction) {
            other.collect();
            heal(other.getHpGiven());
        }
        @Override
        public void interactWith(Staff other, boolean isCellInteraction) {
            other.collect();
            canUseFire = true;
        }
        @Override
        public void interactWith(Key other, boolean isCellInteraction) {
            other.collect();
            keys.add(other.getId());
        }

        @Override
        public void interactWith(Coin other, boolean isCellInteraction) {
            money += other.getValue();
            other.collect();
        }

        @Override
        public void interactWith(Connector other, boolean isCellInteraction) {
            if(!isCellInteraction){
                int keyUsed = other.unLock(keys);
                if(keyUsed!= -1){
                    keys.remove(keyUsed);
                }
            }
            else if(!isDisplacementOccurs()){
                isChangingRoom = true;
                destinationRoom = other.getDestination();
                destinationCoordinates = other.getArrivalCoordinates();
            }
        }
        @Override
        public void interactWith(Turret other, boolean isCellInteraction) {
            other.kill();
        }
    }
}
