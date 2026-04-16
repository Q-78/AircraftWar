package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.elite_Enemy;

public class EliteEnemyFactory implements EnemyFactory {

    @Override
    public AbstractEnemy createEnemy(int locationX, int locationY) {
        return new elite_Enemy(
                locationX,
                locationY,
                0,
                12,
                30
        );
    }
}