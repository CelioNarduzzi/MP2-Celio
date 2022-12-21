package ch.epfl.cs107.play.game.icrogue.actor.enemies;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaGraph;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueAreaGraph;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.Arrow;
import ch.epfl.cs107.play.game.icrogue.handler.ICRogueInteractionHandler;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.List;
import java.util.Queue;

public class Log extends Enemy {
    private AreaGraph graph;
    private DiscreteCoordinates[] targetsPositions;
    private Queue<Orientation> path;
    private int currentDestination = 0;
    private final float COOLDOWN = 1f;
    private float counter;

    /**
     * Default MovableAreaEntity constructor
     *
     * @param area        (Area): Owner area. Not null
     * @param orientation (Orientation): Initial orientation of the entity. Not null
     * @param position    (Coordinate): Initial position of the entity. Not null
     */
    public Log(Area area, Orientation orientation, DiscreteCoordinates position, AreaGraph graph) {
        super(area, orientation, position);
        MOVE_DURATION = 6;
        setSprites(Sprite.extractSprites("zelda/logMonster", 4, 1.5f, 1.5f, this, 32,32, new Orientation[]{
                Orientation.RIGHT,
                Orientation.LEFT,
                Orientation.UP,
                Orientation.DOWN}));
        this.graph = graph;
        targetsPositions = new DiscreteCoordinates[] {
                new DiscreteCoordinates(1,1),
                new DiscreteCoordinates(1,8),
                new DiscreteCoordinates(8,8),
                new DiscreteCoordinates(8,1),
        };
        path = graph.shortestPath(getCurrentMainCellCoordinates(), targetsPositions[0]);
        this.counter = 0;
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
        return false;
    }


    @Override
    public void update(float deltaTime) {
        this.counter+=deltaTime;
        if(path.size() == 0){
            getNextTarget();
        }
        Orientation nextOrientation = path.poll();
        orientate(nextOrientation);
        move(MOVE_DURATION);
        if(isDead()){
            getOwnerArea().unregisterActor(this);
        }

        if(this.counter >= COOLDOWN){
            getOwnerArea().registerActor(new Arrow(getOwnerArea(),
                    getArrowOrientation() , getCurrentMainCellCoordinates().jump(getOrientation().toVector())));
            this.counter = 0;
        }
        super.update(deltaTime);
    }
    private void getNextTarget(){
        DiscreteCoordinates currentTarget = targetsPositions[currentDestination];
        if(currentTarget.equals(getCurrentMainCellCoordinates())){
            if(currentDestination == targetsPositions.length-1){
                currentDestination = 0;
            }
            else {
                currentDestination++;
            }
        }
        path = graph.shortestPath(getCurrentMainCellCoordinates(), targetsPositions[currentDestination]);

    }


    private Orientation getArrowOrientation(){
        switch (getOrientation()){
            case UP -> {return Orientation.RIGHT;}
            case DOWN -> {return Orientation.LEFT;}
            case LEFT -> {return Orientation.UP;}
            case RIGHT -> {return Orientation.DOWN;}
        }
        return null;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICRogueInteractionHandler) v).interactWith(this, isCellInteraction);
    }
}
