package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropFactory;
import edu.hitsz.strategy.CircleShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * Boss敌机
 */
public class boss_enemy extends AbstractEnemy {

    public boss_enemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootNum = 20;
        this.power = 25;
        this.shootStrategy = new CircleShootStrategy(7);
    }

    @Override
    public void forward() {
        locationX += speedX;
        if (locationX <= 0 || locationX >= Main.WINDOW_WIDTH) {
            speedX = -speedX;
        }
    }

    @Override
    public AbstractProp dropProp() {
        return null;
    }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> res = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            double rand = Math.random();
            String type;
            if (rand < 0.2) {
                type = "blood";
            } else if (rand < 0.4) {
                type = "bomb";
            } else if (rand < 0.6) {
                type = "bullet";
            } else if (rand < 0.8) {
                type = "bulletPlus";
            } else {
                type = "freeze";
            }
            res.add(PropFactory.createProp(type, this.getLocationX(), this.getLocationY(), 0, 5));
        }
        return res;
    }
}