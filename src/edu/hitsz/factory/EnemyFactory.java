package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractEnemy;

public interface EnemyFactory {
    AbstractEnemy createEnemy(int locationX, int locationY);
}