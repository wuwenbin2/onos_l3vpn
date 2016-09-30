package org.onosproject.ne;

import org.onlab.util.Identifier;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Immutable representation of a floating IP identifier.
 */
public final class InstanceId extends Identifier<String> {
    // Public construction is prohibited
    private InstanceId(String instaceId) {
        super(checkNotNull(instaceId, "instaceId cannot be null"));
    }

    /**
     * Creates a vpn Instance identifier.
     *
     * @param InstanceId the vpn Instance identifier in string
     * @return object of vpn Instance identifier
     */
    public static InstanceId of(String instaceId) {
        return new InstanceId(instaceId);
    }

    /**
     * Returns the vpn Instance identifier.
     *
     * @return the vpn Instance identifier
     */
    public String instanceId() {
        return identifier;
    }
}
