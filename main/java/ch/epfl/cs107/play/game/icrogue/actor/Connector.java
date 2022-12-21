package ch.epfl.cs107.play.game.icrogue.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.area.ICRogueRoom;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Connector extends AreaEntity {
    public enum ICRogueConnectorState {
        OPEN,
        CLOSED,
        LOCKED,
        INVISIBLE;
    }

    public static int NO_KEY_ID = Integer.MAX_VALUE;
    private ICRogueConnectorState state;
    private String destination;
    private DiscreteCoordinates arrivalCoordinates;
    private int keyID;
    private Sprite sprite;

    /**
     * @param area
     * @param orientation
     * @param position
     * @param state
     * @param destination
     * @param arrivalCoordinates
     * @param keyID
     */
    public Connector(Area area, Orientation orientation, DiscreteCoordinates position, ICRogueConnectorState state, String destination, DiscreteCoordinates arrivalCoordinates, int keyID) {
        super(area, orientation, position);
        this.state = state;
        this.destination = destination;
        this.arrivalCoordinates = arrivalCoordinates;
        this.sprite = selectSprite();
    }

    /**
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate) : Initial position of the entity. Not null
     * @param state       (ICRogueConnectorState) : état du connector
     * @param destination (String) : Nom de la salle d'arrivé
     * @param arrivalCoordinates (DiscreteCoordinates) : Coordonnées dans la salle d'arrivée
     */
    public Connector(Area area, Orientation orientation, DiscreteCoordinates position, ICRogueConnectorState state, String destination, DiscreteCoordinates arrivalCoordinates) {
        this(area, orientation,position, state, destination, arrivalCoordinates, NO_KEY_ID);
    }

    /**
     * Setter des coordonnées dans la salle d'arrivée
     * @param arrivalCoordinates (DiscreteCoordinates) : Coordonnées dans la salle d'arrivée
     */
    public void setArrivalCoordinates(DiscreteCoordinates arrivalCoordinates) {
        this.arrivalCoordinates = arrivalCoordinates;
    }

    /**
     * Getter des coordonnées dans la salle d'arrivée
     * @return (DiscreteCoordinates) : Coordonnées dans la salle d'arrivée
     */
    public DiscreteCoordinates getArrivalCoordinates(){
        return arrivalCoordinates;
    }

    @Override
    public void draw(Canvas canvas) {
        if (state != ICRogueConnectorState.OPEN){
            sprite.draw(canvas);
        }
    }

    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        DiscreteCoordinates coord = getCurrentMainCellCoordinates();
        return List.of(coord, coord.jump(new Vector((getOrientation().ordinal()+1)%2, getOrientation().ordinal()%2)));
    }

    /**
     * Retourne le sprite à afficher en fonction de l'état et de l'orientation du connecteur
     * @return (Sprite) : Sprite du connecteur
     */
    private Sprite selectSprite(){
        Sprite sprite;
        switch (this.state){
            case CLOSED -> {
                sprite = new Sprite("icrogue/door_"+this.getOrientation().opposite().ordinal(), (this.getOrientation().ordinal()+1)%2+1, this.getOrientation().ordinal()%2+1, this);
            }
            case LOCKED -> {
                sprite = new Sprite("icrogue/lockedDoor_"+this.getOrientation().opposite().ordinal(), (this.getOrientation().ordinal()+1)%2+1, this.getOrientation().ordinal()%2+1, this);
            }
            default -> {
                sprite =  new Sprite("icrogue/invisibleDoor_"+this.getOrientation().opposite().ordinal(), (this.getOrientation().ordinal()+1)%2+1, this.getOrientation().ordinal()%2+1, this);
            }
        }
        sprite.setDepth(-1);
        return sprite;
    }

    @Override
    public boolean takeCellSpace() {
        return state != ICRogueConnectorState.OPEN;
    }
    public void setState(ICRogueConnectorState state){
        this.state = state;
        sprite = selectSprite();
    }
    @Override
    public boolean isCellInteractable() {
        return state == ICRogueConnectorState.OPEN;
    }

    @Override
    public boolean isViewInteractable() {
        return true;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler) v).interactWith(this, isCellInteraction);
    }

    /**
     * Ouvre le connecteur s'il n'est pas verrouillé ou invisible
     */
    public void openConnector(){
        if (this.state != ICRogueConnectorState.LOCKED && this.state !=ICRogueConnectorState.INVISIBLE) {
            this.state = ICRogueConnectorState.OPEN;
        }
    }

    /**
     * Ferme le connecteur (sauf si le connecteur est verrouillé)
     */
    public void closeConnector(){
        if(this.state != ICRogueConnectorState.LOCKED) this.state = ICRogueConnectorState.CLOSED;
    }

    /**
     * Verrouille le connecteur
     * @param id (int) : Identificateur de la clé
     */
    public void lockConnector(int id){
        this.state = ICRogueConnectorState.LOCKED;
        this.keyID = id;
        this.sprite = selectSprite();
    }

    /**
     * Déverrouille le connecteur
     * @param playerKeys (List<Integer>) Liste des clés du joueur
     * @return (int) index de la clé utilisé dans la liste de clé du joueur si le joueur possède la clé, -1 si le joueur n'a pas la bonne clé
     */
    public int unLock(List<Integer> playerKeys){
        if(!((ICRogueRoom) getOwnerArea()).isOn())return-1;
        if(keyID == Integer.MAX_VALUE){
            state = ICRogueConnectorState.OPEN;
            return -1;
        }
        for (int i = 0;  i<playerKeys.size() ; i++) {
            if(playerKeys.get(i) == keyID){
                state = ICRogueConnectorState.OPEN;
                return i;
            }
        }
        return -1;
    }

    /**
     * Setter de la destination du connecteur
     * @param destination (String) Nom de la salle d'arrivée
     */
    public void setDestination(String destination){
        this.destination = destination;
    }

    /**
     * Getter de la destination du connecteur
     * @return (String) Nom de la salle d'arrivée
     */
    public String getDestination(){
        return destination;
    }
}