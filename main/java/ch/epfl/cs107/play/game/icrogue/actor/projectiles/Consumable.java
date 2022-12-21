package ch.epfl.cs107.play.game.icrogue.actor.projectiles;

public interface Consumable {
    void consume();

    /**
     * @return (boolean) True si consumé False sinon
     */
    boolean isConsumed();
}
