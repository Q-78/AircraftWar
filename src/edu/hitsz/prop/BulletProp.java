package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.strategy.ScatterShootStrategy;

/**
 * 火力增强道具
 */
public class BulletProp extends AbstractProp {

    public BulletProp(int locationX, int locationY,
                      int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        heroAircraft.setShootNum(3);
        heroAircraft.setShootStrategy(new ScatterShootStrategy());
        System.out.println("FireSupply active!");
    }

}