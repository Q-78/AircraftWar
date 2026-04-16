package edu.hitsz.aircraft;

import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.NoShootStrategy;
/**
 * 普通敌机
 * 不可射击、不掉落道具
 * @author hitsz qhy
 */
public class MobEnemy extends AbstractEnemy {

    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new NoShootStrategy();
    }

    @Override
    public AbstractProp dropProp() {
        return null;
    }
}
