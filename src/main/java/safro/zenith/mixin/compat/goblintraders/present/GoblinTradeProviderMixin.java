package safro.zenith.mixin.compat.goblintraders.present;

import com.mrcrayfish.goblintraders.datagen.GoblinTradeProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import safro.zenith.Zenith;

@Pseudo
@Mixin(value = GoblinTradeProvider.class)
public class GoblinTradeProviderMixin {

    @ModifyConstant(method = "registerGoblinTraderTrades()V", constant = @Constant(intValue = 3, ordinal = 0))
    private static int zenithBreakChance(int old) {
        if (Zenith.enableEnch) {

        }
        return old;
    }

}
