package safro.zenith.village.fletching.arrows;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;

public interface IZenithArrowItem {

	AbstractArrow fromDispenser(Level world, double x, double y, double z);

}
