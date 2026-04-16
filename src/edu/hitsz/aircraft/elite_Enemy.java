package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropFactory;
import edu.hitsz.strategy.StraightShootStrategy;
import java.util.LinkedList;
import java.util.List;

/**
 * 精英敌机
 * 单排直射
 */
public class elite_Enemy extends AbstractEnemy {

    public elite_Enemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootNum = 3;
        this.power = 20;
        this.shootStrategy = new StraightShootStrategy();
    }

    @Override
    public AbstractProp dropProp() {
        if (Math.random() >= 0.5) {
            return null;
        }

        int locationX = this.getLocationX();
        int locationY = this.getLocationY();
        int speedX = 0;
        int speedY = 5;

        double rand = Math.random();
        if (rand < 0.33) {
            return PropFactory.createProp("blood", locationX, locationY, speedX, speedY);
        } else if (rand < 0.66) {
            return PropFactory.createProp("bullet", locationX, locationY, speedX, speedY);
        } else {
            return PropFactory.createProp("bulletPlus", locationX, locationY, speedX, speedY);
        }
    }
}