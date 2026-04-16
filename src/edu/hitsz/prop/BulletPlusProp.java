package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;

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
        System.out.println("FirePlusSupply active!");
    }

}