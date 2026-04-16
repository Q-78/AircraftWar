package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import java.util.LinkedList;
import java.util.List;

/**
 * 散射策略
 */
public class ScatterShootStrategy implements ShootStrategy {

    private final int maxSpreadX;

    public ScatterShootStrategy() {
        this(6);
    }

    public ScatterShootStrategy(int maxSpreadX) {
        this.maxSpreadX = maxSpreadX;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new LinkedList<>();

        int shootNum = aircraft.getShootNum();
        int direction = aircraft.getDirection();
        int baseX = aircraft.getLocationX();
        int baseY = aircraft.getLocationY() + direction * 2;
        int baseSpeedY = aircraft.getSpeedY() + direction * 5;
        int power = aircraft.getPower();

        for (int i = 0; i < shootNum; i++) {
            int speedX;
            if (shootNum == 1) {
                speedX = 0;
            } else {
                speedX = -maxSpreadX + i * (2 * maxSpreadX) / (shootNum - 1);
            }
            int bulletX = baseX + (i * 2 - shootNum + 1) * 8;
            if (aircraft instanceof HeroAircraft) {
                bullets.add(new HeroBullet(bulletX, baseY, speedX, baseSpeedY, power));
            } else {
                bullets.add(new EnemyBullet(bulletX, baseY, speedX, baseSpeedY, power));
            }
        }
        return bullets;
    }
}
