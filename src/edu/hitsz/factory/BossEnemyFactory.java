package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.boss_enemy;

public class BossEnemyFactory implements EnemyFactory {

    @Override
    public AbstractEnemy createEnemy(int locationX, int locationY) {
        return new boss_enemy(
                locationX,
                locationY,
                1,
                8,
                200
        );
    }
}