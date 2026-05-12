package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.CircleShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;

/**
 * 高级火力增强道具
 */
public class BulletPlusProp extends AbstractProp {

    private static final int ACTIVE_TIME_MS = 8000;

    public BulletPlusProp(int locationX, int locationY,
                          int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        heroAircraft.setShootNum(12);
        heroAircraft.setShootStrategy(new CircleShootStrategy(7));

        long endTime = System.currentTimeMillis() + ACTIVE_TIME_MS;
        heroAircraft.setFireEndTime(endTime);

        Runnable recoverTask = () -> {
            try {
                Thread.sleep(ACTIVE_TIME_MS);
                if (System.currentTimeMillis() >= heroAircraft.getFireEndTime()) {
                    heroAircraft.setShootNum(1);
                    heroAircraft.setShootStrategy(new StraightShootStrategy());
                    System.out.println("FirePlusSupply end! Back to straight shoot.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(recoverTask, "bullet-plus-prop-recover-thread").start();

        System.out.println("FirePlusSupply active!");
    }

}
