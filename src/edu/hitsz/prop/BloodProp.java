package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
/**
 * 加血道具
 */
public class BloodProp extends AbstractProp {

    public BloodProp(int locationX, int locationY,
                     int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void activate(HeroAircraft heroAircraft) {
        heroAircraft.increaseHp(30);
        System.out.println("BloodProp activated!");
    }
}