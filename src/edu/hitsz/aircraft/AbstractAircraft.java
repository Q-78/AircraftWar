package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.strategy.NoShootStrategy;
import edu.hitsz.strategy.ShootStrategy;

import java.util.List;

/**
 * 所有种类飞机的抽象父类
 * @author hitsz qhy
 */
public abstract class AbstractAircraft extends AbstractFlyingObject {

    // 最大生命值
    protected int maxHp;
    protected int hp;

    /** 射击相关属性，供策略类读取 */
    protected int shootNum = 1;
    protected int power = 0;
    /** 子弹射击方向（英雄机向上 -1，敌机向下 1） */
    protected int direction = 1;

    /** 当前射击策略 */
    protected ShootStrategy shootStrategy = new NoShootStrategy();

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
    }

    public void decreaseHp(int decrease){
        hp -= decrease;
        if (hp <= 0) {
            hp = 0;
            vanish();
        }
    }

    /**
     * 增加生命值，且不超过最大生命值
     */
    public void increaseHp(int increase) {
        hp += increase;
        if (hp > maxHp) {
            hp = maxHp;
        }
    }

    public void setHp(int hp) {
        this.hp = hp;
        if (this.hp > maxHp) {
            this.hp = maxHp;
        }
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getShootNum() {
        return shootNum;
    }

    public void setShootNum(int shootNum) {
        this.shootNum = shootNum;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public ShootStrategy getShootStrategy() {
        return shootStrategy;
    }

    public void setShootStrategy(ShootStrategy shootStrategy) {
        this.shootStrategy = shootStrategy;
    }

    /**
     * 飞机射击方法
     * @return
     * 可射击对象需实现，返回子弹列表
     * 非可射击对象空实现，返回空列表
     */
    /**
     * 飞机射击方法
     */
    public List<BaseBullet> shoot() {
        return shootStrategy.shoot(this);
    }

}