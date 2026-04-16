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
        if (instance == null) {
            instance = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
        }
    }

    /**
     * 获取唯一实例
     */
    public static HeroAircraft getInstance() {
        return instance;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }
}
