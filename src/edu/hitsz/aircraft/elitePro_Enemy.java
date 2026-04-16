package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropFactory;
import edu.hitsz.strategy.ScatterShootStrategy;

import java.util.LinkedList;
import java.util.List;
/**
 * 高级精英敌机
 * 散射
 */
public class elitePro_Enemy extends AbstractEnemy {

    public elitePro_Enemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootNum = 3;
        this.power = 25;
        this.shootStrategy = new ScatterShootStrategy(2);
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
        if (rand < 0.2) {
            return PropFactory.createProp("blood", locationX, locationY, speedX, speedY);
        } else if (rand < 0.4) {
            return PropFactory.createProp("bomb", locationX, locationY, speedX, speedY);
        } else if (rand < 0.6) {
            return PropFactory.createProp("bullet", locationX, locationY, speedX, speedY);
        } else if (rand < 0.8) {
            return PropFactory.createProp("bulletPlus", locationX, locationY, speedX, speedY);
        } else {
            return PropFactory.createProp("freeze", locationX, locationY, speedX, speedY);
        }
    }
}