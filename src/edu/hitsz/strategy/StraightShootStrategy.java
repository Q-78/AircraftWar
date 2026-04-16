package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 直射策略
 */
public class StraightShootStrategy implements ShootStrategy {

    private final int xStep;

    public StraightShootStrategy() {
        this(10);
    }

    public StraightShootStrategy(int xStep) {
        this.xStep = xStep;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new LinkedList<>();

        int shootNum = aircraft.getShootNum();
        int direction = aircraft.getDirection();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY() + direction * 2;
        int speedY = aircraft.getSpeedY() + direction * 5;
        int power = aircraft.getPower();

        for (int i = 0; i < shootNum; i++) {
            int bulletX = x + (i * 2 - shootNum + 1) * xStep;
            if (aircraft instanceof HeroAircraft) {
                bullets.add(new HeroBullet(bulletX, y, 0, speedY, power));
            } else {
                bullets.add(new EnemyBullet(bulletX, y, 0, speedY, power));
            }
        }
        return bullets;
    }
}
