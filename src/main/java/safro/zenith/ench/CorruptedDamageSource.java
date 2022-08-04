package safro.zenith.ench;

import net.minecraft.world.damagesource.DamageSource;

public class CorruptedDamageSource extends DamageSource {
    public static final DamageSource DEFAULT = new CorruptedDamageSource().bypassArmor().bypassMagic();

    public CorruptedDamageSource() {
        super("apoth_corrupted");
    }

}
