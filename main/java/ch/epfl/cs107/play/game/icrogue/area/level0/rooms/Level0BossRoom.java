package ch.epfl.cs107.play.game.icrogue.area.level0.rooms;

import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icrogue.ICRogue;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Enemy;
import ch.epfl.cs107.play.game.icrogue.actor.enemies.Turret;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;
import java.util.List;

public class Level0BossRoom extends Level0EnemyRoom{

    public Level0BossRoom(DiscreteCoordinates roomCoordinates) {
        super(roomCoordinates);
        List<Enemy> enemies= new ArrayList<>();
        enemies.add(new Turret(this, Orientation.UP, new DiscreteCoordinates(1, 8), new Orientation[]{Orientation.DOWN, Orientation.RIGHT}));
        enemies.add(new Turret(this, Orientation.UP, new DiscreteCoordinates(8, 1), new Orientation[]{Orientation.UP, Orientation.LEFT}));
        setEnemies(enemies);

    }
}
