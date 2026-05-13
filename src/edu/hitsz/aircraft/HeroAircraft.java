package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.strategy.StraightShootStrategy;

import java.util.LinkedList;
import java.util.List;

/**
 * 英雄飞机，游戏玩家操控
 * @author hitsz qhy
 */
public class HeroAircraft extends AbstractAircraft {

// ================= 单例部分 =================

    /** 火力道具效果结束时间，用于避免连续吃道具时前一个线程提前恢复火力 */
    private volatile long fireEndTime = 0;

    /** 无敌冲刺结束时间，System.currentTimeMillis() 小于该值时英雄机免疫伤害 */
    private volatile long invincibleEndTime = 0;

    // 唯一实例
    private static HeroAircraft instance;

    /**
     * 私有构造方法，禁止外部 new
     */
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootNum = 1;
        this.power = 30;
        this.direction = -1;
        this.shootStrategy = new StraightShootStrategy();
    }

    /**
     * 初始化（只调用一次）
     */
    public static void init(int locationX, int locationY, int speedX, int speedY, int hp) {
        // 每开一局游戏都重新初始化，避免上一局 game over 后血量、位置和火力状态残留
        instance = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
    }

    /**
     * 获取唯一实例
     */
    public static HeroAircraft getInstance() {
        return instance;
    }

    public long getFireEndTime() {
        return fireEndTime;
    }

    public void setFireEndTime(long fireEndTime) {
        this.fireEndTime = fireEndTime;
    }

    public void activateInvincible(long durationMs) {
        long endTime = System.currentTimeMillis() + durationMs;
        if (endTime > invincibleEndTime) {
            invincibleEndTime = endTime;
        }
    }

    public boolean isInvincible() {
        return System.currentTimeMillis() < invincibleEndTime;
    }

    public long getInvincibleRemainMs() {
        long remain = invincibleEndTime - System.currentTimeMillis();
        return Math.max(0, remain);
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }
}
