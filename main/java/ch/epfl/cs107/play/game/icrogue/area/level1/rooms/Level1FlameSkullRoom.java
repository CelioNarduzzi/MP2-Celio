package ch.epfl.cs107.play.game.icrogue.area.level1.rooms;

import ch.epfl.cs107.play.game.areagame.AreaGraph;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.ICRogueAreaGraph;
import ch.epfl.cs107.play.game.icrogue.actor.ICRoguePlayer;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Enemy;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.FlameSkull;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class Level1FlameSkullRoom extends Level1EnemyRoom {
    private AreaGraph graph = new ICRogueAreaGraph(10, 10);
    public Level1FlameSkullRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
        List<Enemy> enemies= new ArrayList<>();
        enemies.add(new FlameSkull(this, Orientation.UP, new DiscreteCoordinates(8,1), graph));
        setEnemies(enemies);
    }
}
