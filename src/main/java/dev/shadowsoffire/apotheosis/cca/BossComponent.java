package dev.shadowsoffire.apotheosis.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;

public class BossComponent implements Component {
    private boolean isBoss = false;
    private String rarity;

    private String miniBoss;
    private float minibossLuck = 0f;

    @Override
    public void readFromNbt(CompoundTag tag) {
        this.isBoss = tag.getBoolean("is_boss");
        this.rarity = tag.getString("boss_rarity");
        this.miniBoss = tag.getString("miniboss");
        this.minibossLuck = tag.getFloat("miniboss_luck");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean("is_boss", isBoss);
        tag.putString("boss_rarity", rarity);
        tag.putString("miniboss", miniBoss);
        tag.putString("miniboss_luck", miniBoss);
    }

    public void setIsBoss(boolean boss) {
        isBoss = boss;
    }

    public boolean getIsBoss() {
        return isBoss;
    }

    public String getMiniBoss() {
        return miniBoss;
    }

    public void setMiniBoss(String miniBoss) {
        this.miniBoss = miniBoss;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarityAndBoss(String rarity) {
        this.rarity = rarity;
        isBoss = true;
    }

    public float getMinibossLuck() {
        return minibossLuck;
    }

    public void setMinibossLuck(float minibossLuck) {
        this.minibossLuck = minibossLuck;
    }
}
