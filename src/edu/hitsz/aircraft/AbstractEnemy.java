package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 敌机抽象父类
 * 所有敌机统一继承该类
 *
 * @author hitsz
 */
public abstract class AbstractEnemy extends AbstractAircraft {

    public AbstractEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    /**
     * 敌机统一向下飞行
     */
    @Override
    public void forward() {
        super.forward();

        // 飞出界面后消失
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    /**
     * 默认敌机不射击
     */
    @Override
    public List<BaseBullet> shoot() {
        return new LinkedList<>();
    }

}