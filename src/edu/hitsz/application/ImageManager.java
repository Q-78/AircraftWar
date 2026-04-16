package edu.hitsz.application;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.aircraft.elite_Enemy;
import edu.hitsz.aircraft.elitePlus_Enemy;
import edu.hitsz.aircraft.elitePro_Enemy;
import edu.hitsz.aircraft.boss_enemy;

import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;

import edu.hitsz.prop.BloodProp;
import edu.hitsz.prop.BombProp;
import edu.hitsz.prop.BulletProp;
import edu.hitsz.prop.BulletPlusProp;
import edu.hitsz.prop.FreezeProp;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 综合管理图片的加载，访问
 * 提供图片的静态访问方法
 * @author hitsz
 */
public class ImageManager {

    /**
     * 类名-图片 映射
     */
    private static final Map<String, BufferedImage> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static BufferedImage BACKGROUND_IMAGE;

    public static BufferedImage HERO_IMAGE;

    public static BufferedImage HERO_BULLET_IMAGE;
    public static BufferedImage ENEMY_BULLET_IMAGE;

    public static BufferedImage MOB_ENEMY_IMAGE;
    public static BufferedImage ELITE_ENEMY_IMAGE;
    public static BufferedImage ELITE_PLUS_ENEMY_IMAGE;
    public static BufferedImage ELITE_PRO_ENEMY_IMAGE;
    public static BufferedImage BOSS_ENEMY_IMAGE;

    // 道具图片
    public static BufferedImage BLOOD_PROP_IMAGE;
    public static BufferedImage BOMB_PROP_IMAGE;
    public static BufferedImage BULLET_PROP_IMAGE;
    public static BufferedImage BULLETPLUS_PROP_IMAGE;
    public static BufferedImage FREEZE_PROP_IMAGE;

    static {
        try {

            // 背景
            BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg.jpg"));

            // 英雄机
            HERO_IMAGE = ImageIO.read(new FileInputStream("src/images/hero.png"));

            // 敌机
            MOB_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/mob.png"));
            ELITE_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elite.png"));
            ELITE_PLUS_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elitePlus.png"));
            ELITE_PRO_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elitePro.png"));
            BOSS_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/boss.png"));

            // 子弹
            HERO_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_hero.png"));
            ENEMY_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_enemy.png"));

            // 道具
            BLOOD_PROP_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_blood.png"));
            BOMB_PROP_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bomb.png"));
            BULLET_PROP_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bullet.png"));
            BULLETPLUS_PROP_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bulletPlus.png"));
            FREEZE_PROP_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_freeze.png"));

            /**
             * 注册映射
             */

            // 飞机
            CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
            CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(elite_Enemy.class.getName(), ELITE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(elitePlus_Enemy.class.getName(), ELITE_PLUS_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(elitePro_Enemy.class.getName(), ELITE_PRO_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(boss_enemy.class.getName(), BOSS_ENEMY_IMAGE);

            // 子弹
            CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);

            // 道具
            CLASSNAME_IMAGE_MAP.put(BloodProp.class.getName(), BLOOD_PROP_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BombProp.class.getName(), BOMB_PROP_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BulletProp.class.getName(), BULLET_PROP_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BulletPlusProp.class.getName(), BULLETPLUS_PROP_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FreezeProp.class.getName(), FREEZE_PROP_IMAGE);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static BufferedImage get(String className) {
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static BufferedImage get(Object obj) {
        if (obj == null) {
            return null;
        }
        return get(obj.getClass().getName());
    }
}