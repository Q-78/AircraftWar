package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.elitePlus_Enemy;

public class ElitePlusEnemyFactory implements EnemyFactory {

    @Override
    public AbstractEnemy createEnemy(int locationX, int locationY) {
        return new elitePlus_Enemy(
                locationX,
                locationY,
                2,
                12,
                40
        );
    }
}