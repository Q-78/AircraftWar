package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.application.EffectScheduler;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.observer.EffectObserver;

import java.util.LinkedList;
import java.util.List;

/**
 * 敌机抽象父类
 * 所有敌机统一继承该类
 *
 * @author hitsz
 */
public abstract class AbstractEnemy extends AbstractAircraft implements EffectObserver {

    public AbstractEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.direction = 1;
    }

    /**
     * 敌机死亡后的掉落逻辑
     * @return 掉落的道具；若不掉落则返回 null
     */
    public abstract AbstractProp dropProp();


    /**
     * 默认单个掉落；Boss 可重写为多个掉落
     */
    public List<AbstractProp> dropProps() {
        List<AbstractProp> res = new LinkedList<>();
        AbstractProp prop = dropProp();
        if (prop != null) {
            res.add(prop);
        }
        return res;
    }


    public int getScoreValue() {
        return 10;
    }

    @Override
    public void onBomb() {
        vanish();
    }

    @Override
    public void onFreeze() {
        freezeTemporarily(5000);
    }

    protected void freezePermanently() {
        setSpeed(0, 0);
    }

    protected void freezeTemporarily(int durationMs) {
        final int oldSpeedX = speedX;
        final int oldSpeedY = speedY;
        setSpeed(0, 0);
        EffectScheduler.schedule(() -> {
            if (!notValid()) {
                setSpeed(oldSpeedX, oldSpeedY);
            }
        }, durationMs);
    }

    protected void slowTemporarily(double ratio, int durationMs) {
        final int oldSpeedX = speedX;
        final int oldSpeedY = speedY;
        setSpeed((int) Math.round(oldSpeedX * ratio), (int) Math.round(oldSpeedY * ratio));
        EffectScheduler.schedule(() -> {
            if (!notValid()) {
                setSpeed(oldSpeedX, oldSpeedY);
            }
        }, durationMs);
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

}