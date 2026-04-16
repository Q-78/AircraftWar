package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropFactory;
import java.util.LinkedList;
import java.util.List;

/**
 * Boss敌机
 */
public class boss_enemy extends AbstractEnemy {

    public boss_enemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public AbstractProp dropProp() {
        return null;
    }

}