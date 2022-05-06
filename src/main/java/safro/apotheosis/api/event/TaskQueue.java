package safro.apotheosis.api.event;

import org.apache.commons.lang3.tuple.Pair;
import safro.apotheosis.Apotheosis;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.function.BooleanSupplier;

public class TaskQueue {
    private static final Queue<Pair<String, BooleanSupplier>> TASKS = new ArrayDeque<>();

    public static void tick() {
        Iterator<Pair<String, BooleanSupplier>> it = TASKS.iterator();
        Pair<String, BooleanSupplier> current = null;
        while (it.hasNext()) {
            current = it.next();
            try {
                if (current.getRight().getAsBoolean()) it.remove();
            } catch (Exception ex) {
                Apotheosis.LOGGER.error("An exception occurred while running a ticking task with ID {}.  It will be terminated.", current.getLeft());
                it.remove();
                ex.printStackTrace();
            }
        }
    }

    public static void stopped() {
        TASKS.clear();
    }

    public static void started() {
        TASKS.clear();
    }

    public static void submitTask(String id, BooleanSupplier task) {
        TASKS.add(Pair.of(id, task));
    }
}
