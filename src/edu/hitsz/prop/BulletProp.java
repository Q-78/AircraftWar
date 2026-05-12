package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.ScatterShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;

/**
 * 火力增强道具
 */
public class BulletProp extends AbstractProp {

    private static final int ACTIVE_TIME_MS = 5000;

    public BulletProp(int locationX, int locationY,
                      int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        heroAircraft.setShootNum(3);
        heroAircraft.setShootStrategy(new ScatterShootStrategy());

        long endTime = System.currentTimeMillis() + ACTIVE_TIME_MS;
        heroAircraft.setFireEndTime(endTime);

        Runnable recoverTask = () -> {
            try {
                Thread.sleep(ACTIVE_TIME_MS);
                if (System.currentTimeMillis() >= heroAircraft.getFireEndTime()) {
                    heroAircraft.setShootNum(1);
                    heroAircraft.setShootStrategy(new StraightShootStrategy());
                    System.out.println("FireSupply end! Back to straight shoot.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(recoverTask, "bullet-prop-recover-thread").start();

        System.out.println("FireSupply active!");
    }

}
