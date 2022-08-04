package safro.zenith.village.compat;

import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import safro.zenith.village.fletching.FletchingContainer;

public class FletchingTransferHandler implements TransferHandler {

    @Override
    public Result handle(Context context) {
        if (context.getDisplay() instanceof FletchingDisplay && context.getMenu() instanceof FletchingContainer) {
            if (!context.isActuallyCrafting()) {
                return Result.createSuccessful();
            }
        }
        return Result.createNotApplicable();
    }
}
