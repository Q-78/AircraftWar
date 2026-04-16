package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 环射策略
 */
public class CircleShootStrategy implements ShootStrategy {

    private final int radiusSpeed;

    public CircleShootStrategy() {
        this(6);
    }

    public CircleShootStrategy(int radiusSpeed) {
        this.radiusSpeed = radiusSpeed;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new LinkedList<>();

        int shootNum = aircraft.getShootNum();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY();
        int power = aircraft.getPower();

        for (int i = 0; i < shootNum; i++) {
            double angle = 2 * Math.PI * i / shootNum;
            int speedX = (int) Math.round(radiusSpeed * Math.cos(angle));
            int speedY = (int) Math.round(radiusSpeed * Math.sin(angle));

            if (aircraft instanceof HeroAircraft) {
                bullets.add(new HeroBullet(x, y, speedX, speedY, power));
            } else {
                bullets.add(new EnemyBullet(x, y, speedX, speedY, power));
            }
        }
        return bullets;
    }
}
