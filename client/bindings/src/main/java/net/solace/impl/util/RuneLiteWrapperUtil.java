package net.solace.impl.util;

import net.solace.api.domain.RuneLiteWrapper;

import java.util.Objects;

public class RuneLiteWrapperUtil {
    public static <T> int getHashCode(RuneLiteWrapper<T> wrapper) {
        return wrapper.getWrapped().hashCode();
    }

    public static <T> boolean isEqual(RuneLiteWrapper<T> wrapper, Object other) {
        if (other == null || wrapper == null) {
            return false;
        }

        if (other == wrapper) {
            return true;
        }

        var rlClass = wrapper.getWrapped();
        if (other.getClass() == rlClass.getClass()) {
            return rlClass.equals(other);
        }

        if (other.getClass() != wrapper.getClass()) {
            return false;
        }

        return Objects.equals(rlClass, ((RuneLiteWrapper<?>) other).getWrapped());
    }
}
