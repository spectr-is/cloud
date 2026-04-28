package org.incendo.cloud.internal;

import io.leangen.geantyref.GenericTypeReflector;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global cache for {@link GenericTypeReflector#isSuperType(Type, Type)} results
 * <p>
 * Generic type resolutions are done relatively often on some platforms (incl. Brigadier) which
 * becomes heavy very quickly.
 *
 * @since 2.0.0
 */
@API(status = API.Status.INTERNAL, since = "2.0.0")
public final class SuperTypeCache {

    private static final Map<Type, Map<Class<?>, Boolean>> CACHE = new ConcurrentHashMap<>();

    private SuperTypeCache() {}

    /**
     * Returns whether the provided type is a super-type of {@code subClass}.
     *
     * @param superType the super type
     * @param subClass the concrete class to test
     * @return {@code true} if {@code superType} is assignable from {@code subClass}
     */
    public static boolean isSuperType(final @NonNull Type superType, final @NonNull Class<?> subClass) {
        Map<Class<?>, Boolean> inner = CACHE.get(superType);
        if (inner == null) {
            inner = CACHE.computeIfAbsent(superType, k -> new ConcurrentHashMap<>());
        }
        final Boolean cached = inner.get(subClass);
        if (cached != null) {
            return cached;
        }
        final boolean result = GenericTypeReflector.isSuperType(superType, subClass);
        inner.put(subClass, result);
        return result;
    }
}
