package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropFactory;
import java.util.LinkedList;
import java.util.List;

/**
 * 精英敌机
 * 可射击
 */
public class elite_Enemy extends AbstractEnemy {

    // 每次发射子弹数
    private int shootNum = 3;

    // 子弹威力
    private int power = 20;

    // 子弹射击方向（向下：1）
    private int direction = 1;


    public elite_Enemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
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

    @Override
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();

        // 单排子弹：横向排开，统一向下
        for (int i = 0; i < shootNum; i++) {
            res.add(new EnemyBullet(
                    locationX + (i * 2 - shootNum + 1) * 10,
                    locationY + direction * 2,
                    0,
                    speedY + 5,
                    power
            ));
        }

        return res;
    }
}