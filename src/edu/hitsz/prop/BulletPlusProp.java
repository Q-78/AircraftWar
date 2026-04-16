package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.CircleShootStrategy;

/**
 * 高级火力增强道具
 */
public class BulletPlusProp extends AbstractProp {

    public BulletPlusProp(int locationX, int locationY,
                          int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        heroAircraft.setShootNum(12);
        heroAircraft.setShootStrategy(new CircleShootStrategy(7));
        System.out.println("FirePlusSupply active!");
    }

}