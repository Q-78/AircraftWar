package edu.hitsz.prop;

/**
 * 道具简单工厂
 * 根据传入类型统一创建不同道具对象
 */
public class PropFactory {

    /**
     * 创建道具
     *
     * @param type 道具类型
     * @param locationX 道具横坐标
     * @param locationY 道具纵坐标
     * @param speedX 横向速度
     * @param speedY 纵向速度
     * @return 对应的道具对象
     */
    public static AbstractProp createProp(String type,
                                          int locationX, int locationY,
                                          int speedX, int speedY) {
        switch (type) {
            case "blood":
                return new BloodProp(locationX, locationY, speedX, speedY);
            case "bomb":
                return new BombProp(locationX, locationY, speedX, speedY);
            case "bullet":
                return new BulletProp(locationX, locationY, speedX, speedY);
            case "bulletPlus":
                return new BulletPlusProp(locationX, locationY, speedX, speedY);
            case "freeze":
                return new FreezeProp(locationX, locationY, speedX, speedY);
            default:
                throw new IllegalArgumentException("Unknown prop type: " + type);
        }
    }
}