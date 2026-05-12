package edu.hitsz.observer;

/**
 * 道具效果观察者接口：敌机和敌机子弹作为观察者，
 * 在炸弹/冰冻道具触发时根据自身类型作出不同响应。
 */
public interface EffectObserver {
    /** 炸弹道具触发后的响应 */
    void onBomb();

    /** 冰冻道具触发后的响应 */
    void onFreeze();
}
