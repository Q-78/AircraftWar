package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropFactory;
import java.util.LinkedList;
import java.util.List;
/**
 * 高级精英敌机
 */
public class elitePro_Enemy extends AbstractEnemy {

    public elitePro_Enemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();

        int power = 25;

        // 中间一发：直射向下
        res.add(new EnemyBullet(
                this.locationX,
                this.locationY + 2,
                0,
                this.speedY + 6,
                power
        ));

        // 左侧一发：左下
        res.add(new EnemyBullet(
                this.locationX - 10,
                this.locationY + 2,
                -2,
                this.speedY + 6,
                power
        ));

        // 右侧一发：右下
        res.add(new EnemyBullet(
                this.locationX + 10,
                this.locationY + 2,
                2,
                this.speedY + 6,
                power
        ));

        return res;
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