package dev.shadowsoffire.apotheosis.compat;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class MixinCanceller implements com.bawnorton.mixinsquared.api.MixinCanceller { // Just in case :/

    public static final Logger LOGGER = LogManager.getLogger("Zenith : Mixin Canceller");
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (mixinClassName.equals("net.soulsweaponry.mixin.EnchantmentHelperMixin")) {
            LOGGER.info("Cancelling mixin net.soulsweaponry.mixin.EnchantmentHelperMixin, replaced with slightly different implementation");
            return true;
        }

        return false;
    }
}
