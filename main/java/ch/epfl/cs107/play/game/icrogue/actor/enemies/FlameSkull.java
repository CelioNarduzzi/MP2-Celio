package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaGraph;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.RandomHelper;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.signal.logic.Or;

import java.util.List;
import java.util.Queue;

public class FlameSkull extends Enemy implements Interactor {
    private AreaGraph graph;
    private Queue<Orientation> path;
    private ICRoguePlayer player;
    private FlameSkullInteractionHandler handler;
    private int damage = 1;

    private float count = 0;
    private final float PLAYER_HIT_COOLDOWN = MOVE_DURATION;
    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public FlameSkull(Area area, Orientation orientation, DiscreteCoordinates position, AreaGraph graph) {
        super(area, orientation, position);
        MOVE_DURATION = 6;
        setSprites(Sprite.extractSprites("zelda/flameskull", 4, 1.5f, 1.5f, this, 32,32, new Orientation[]{
                Orientation.UP,
                Orientation.LEFT,
                Orientation.DOWN,
                Orientation.RIGHT}));
        setSpritesDepth(100);
        this.graph = graph;

        path = graph.shortestPath(getCurrentMainCellCoordinates(), getTarget());
        handler = new FlameSkullInteractionHandler();
        MOVE_DURATION = 3;
    }

    @Override
    public boolean takeCellSpace() {
        return false;
    }

    @Override
    public boolean isCellInteractable() {
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }


    private Orientation getOrientationPathNull(){
        int x = getCurrentMainCellCoordinates().x;
        int y = getCurrentMainCellCoordinates().y;

        if(x <= 1){
            return Orientation.RIGHT;
        }
        if (x>=9){
            return Orientation.LEFT;
        }
        if (y<=1){
            return Orientation.UP;
        }
        return Orientation.DOWN;
    }

    @Override
    public void update(float deltaTime) {
        count += deltaTime;
        if(path == null || path.size() == 0){
            updatePath();
        }
        if(!isDisplacementOccurs()){
            //On vérifie, car il est possible qu'aucun path n'ait été trouvé
            if(path != null) {
                orientate(path.poll());
            }
            else {
                orientate(getOrientationPathNull());
            }
            move(MOVE_DURATION);
        }
        if(isDead()){
            getOwnerArea().unregisterActor(this);
        }
        super.update(deltaTime);
    }

    public DiscreteCoordinates getTarget(){
        if(player == null || count <= PLAYER_HIT_COOLDOWN){
            return getRandomTarget();
        }
        else if (player.getCurrentCells().get(0).equals(getCurrentMainCellCoordinates())){
            //choisir une direction au hazard
            int direction = RandomHelper.enemyGenerator.nextInt(4);
            Orientation orientation = Orientation.values()[direction];
            this.count = 0;
            return getCurrentMainCellCoordinates().jump(orientation.toVector().mul(2));
        }
        else {
            return player.getCurrentCells().get(0);
        }
    }

    private DiscreteCoordinates getRandomTarget() {
        int x = RandomHelper.enemyGenerator.nextInt(8) + 1;
        int y = RandomHelper.enemyGenerator.nextInt(8) + 1;
        return new DiscreteCoordinates(x, y);
    }

    public void updatePath(){
        DiscreteCoordinates target = getTarget();
        path = graph.shortestPath(getCurrentMainCellCoordinates(), target);
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler) v).interactWith(this, isCellInteraction);
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return getCurrentMainCellCoordinates().getNeighbours();
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return true;
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(handler, isCellInteraction);
    }
    private class FlameSkullInteractionHandler implements ICRogueInteractionHandler {

        @Override
        public void interactWith(ICRoguePlayer other, boolean isCellInteraction) {
            if(isCellInteraction) {
                if(count >=PLAYER_HIT_COOLDOWN){
                    other.takeDamages(damage);
                    count = 0;
                }
                if(player == null){
                    player = other;
                }
            }
            else {
                player = other;
            }
        }
    }
}
