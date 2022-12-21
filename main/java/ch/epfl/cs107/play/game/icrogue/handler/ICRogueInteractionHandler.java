package ch.epfl.cs107.play.game.icrogue.handler;

import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icrogue.ICRogueBehavior;
import ch.epfl.cs107.play.game.icrogue.actor.Connector;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Enemy;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.FlameSkull;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Log;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.game.icrogue.actor.items.*;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.Arrow;
import ch.epfl.cs107.play.game.icrogue.actor.projectiles.Fire;

public interface ICRogueInteractionHandler extends AreaInteractionVisitor {
    default void interactWith(ICRogueBehavior.ICRogueCell other, boolean isCellInteraction) {}
    default void interactWith(Connector other, boolean isCellInteraction) {}
    default void interactWith(ICRoguePlayer other, boolean isCellInteraction) {}

    //ITEMS
    default void interactWith(Cherry other, boolean isCellInteraction) {}
    default void interactWith(Staff other, boolean isCellInteraction) {}
    default void interactWith(Key other, boolean isCellInteraction) {}
    default void interactWith(Heal other, boolean isCellInteraction){}
    default void interactWith(Coin other, boolean isCellInteraction){}

    //PROJECTILES
    default void interactWith(Fire other, boolean isCellInteraction) {}
    default void interactWith(Arrow other, boolean isCellInteraction) {}

    //ENEMIES
    default void interactWith(Turret other, boolean isCellInteraction) {}
    default void interactWith(Log other, boolean isCellInteraction){}
    default void interactWith(FlameSkull other, boolean isCellInteraction){}

}
