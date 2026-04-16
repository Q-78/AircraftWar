package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropFactory;
import edu.hitsz.strategy.StraightShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 强化精英敌机
 * s双排直射
 */
public class elitePlus_Enemy extends AbstractEnemy {

    public elitePlus_Enemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootNum = 2;
        this.power = 20;
        this.shootStrategy = new StraightShootStrategy(15);
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
        if (rand < 0.25) {
            return PropFactory.createProp("blood", locationX, locationY, speedX, speedY);
        } else if (rand < 0.5) {
            return PropFactory.createProp("bomb", locationX, locationY, speedX, speedY);
        } else if (rand < 0.75) {
            return PropFactory.createProp("bullet", locationX, locationY, speedX, speedY);
        } else {
            return PropFactory.createProp("bulletPlus", locationX, locationY, speedX, speedY);
        }
    }

}