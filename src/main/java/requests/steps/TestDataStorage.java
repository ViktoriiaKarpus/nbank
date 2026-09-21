package requests.steps;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TestDataStorage {

    private static final ThreadLocal<Set<Long>> createdUserIds =
            ThreadLocal.withInitial(HashSet::new);

    private TestDataStorage() {
    }

    public static void registerUser(long userId) {
        createdUserIds.get().add(userId);
    }

    public static Set<Long> getCreatedUserIds() {
        return Collections.unmodifiableSet(createdUserIds.get());
    }

    public static void clear() {
        createdUserIds.get().clear();
    }
}
