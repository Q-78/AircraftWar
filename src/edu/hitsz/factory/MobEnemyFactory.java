package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.MobEnemy;

public class MobEnemyFactory implements EnemyFactory {

    @Override
    public AbstractEnemy createEnemy(int locationX, int locationY) {
        return new MobEnemy(
                locationX,
                locationY,
                0,
                10,
                30
        );
    }
}