package safro.apotheosis.api;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import safro.apotheosis.Apotheosis;

/**
 * Simple reload listener that allows for lambda usage.
 */
public class RunnableReloader extends SimplePreparableReloadListener<Object> implements IdentifiableResourceReloadListener {
    protected final Runnable r;
    protected final String name;

    public RunnableReloader(Runnable r, String name) {
        this.r = r;
        this.name = name;
    }

    @Override
    protected Object prepare(ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        return null;
    }

    @Override
    protected void apply(Object objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        this.r.run();
    }

    public static RunnableReloader of(Runnable r, String name) {
        return new RunnableReloader(r, name);
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(Apotheosis.MODID, name);
    }

    public static void add(Runnable r, String name) {
        RunnableReloader reloader = of(r, name);
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(reloader);
    }
}
