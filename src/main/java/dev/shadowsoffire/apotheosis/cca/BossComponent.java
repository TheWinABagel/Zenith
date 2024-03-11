package dev.shadowsoffire.apotheosis.cca;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;

public class BossComponent implements Component {
    private boolean isBoss = false;
    private String rarity = "";

    private String miniBoss = "";
    private float minibossLuck = 0f;

    @Override
    public void readFromNbt(CompoundTag tag) {
        if (tag.contains("is_boss")) this.isBoss = tag.getBoolean("is_boss");
        if (tag.contains("boss_rarity")) this.rarity = tag.getString("boss_rarity");
        if (tag.contains("miniboss")) this.miniBoss = tag.getString("miniboss");
        if (tag.contains("miniboss_luck")) this.minibossLuck = tag.getFloat("miniboss_luck");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        if (isBoss)
            tag.putBoolean("is_boss", isBoss);
        if (!rarity.isEmpty())
            tag.putString("boss_rarity", rarity);
        if (!miniBoss.isEmpty())
            tag.putString("miniboss", miniBoss);
        if (!(minibossLuck != 0f))
            tag.putFloat("miniboss_luck", minibossLuck);
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
