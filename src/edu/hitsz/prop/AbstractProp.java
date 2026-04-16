package edu.hitsz.prop;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;

/**
 * 道具抽象父类
 *
 * 所有道具统一继承该类
 *
 * @author hitsz
 */
public abstract class AbstractProp extends AbstractFlyingObject {

    public AbstractProp(int locationX, int locationY,
                        int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }
    public abstract void activate(HeroAircraft heroAircraft);
    /**
     * 道具向下移动
     */
    @Override
    public void forward() {
        super.forward();

        // 飞出屏幕后消失
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

}