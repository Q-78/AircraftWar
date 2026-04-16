package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.elitePro_Enemy;

public class EliteProEnemyFactory implements EnemyFactory {

    @Override
    public AbstractEnemy createEnemy(int locationX, int locationY) {
        return new elitePro_Enemy(
                locationX,
                locationY,
                2,
                14,
                50
        );
    }
}