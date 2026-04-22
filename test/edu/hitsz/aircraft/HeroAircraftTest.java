package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeroAircraftTest {

    private HeroAircraft hero;

    @BeforeEach
    void setUp() {
        // 如果实例还没创建，就初始化
        HeroAircraft.init(200, 600, 0, 0, 100);
        hero = HeroAircraft.getInstance();

        // 每次测试前尽量把状态恢复一下
        hero.setHp(100);
    }

    @Test
    @DisplayName("测试英雄机单例是否成立")
    void testGetInstanceSingleton() {
        HeroAircraft anotherHero = HeroAircraft.getInstance();
        assertSame(hero, anotherHero);
    }

    @Test
    @DisplayName("测试 decreaseHp 方法")
    void testDecreaseHp() {
        hero.decreaseHp(30);
        assertEquals(70, hero.getHp());
    }

    @Test
    @DisplayName("测试 increaseHp 方法")
    void testIncreaseHp() {
        hero.decreaseHp(50);
        hero.increaseHp(30);
        assertEquals(80, hero.getHp());
    }

    @Test
    @DisplayName("测试 increaseHp 不超过最大生命值")
    void testIncreaseHpNotExceedMaxHp() {
        hero.increaseHp(50);
        assertEquals(100, hero.getHp());
    }

    @Test
    @DisplayName("测试英雄机射击")
    void testShoot() {
        List<BaseBullet> bullets = hero.shoot();

        assertNotNull(bullets);
        assertEquals(1, bullets.size());
    }
}