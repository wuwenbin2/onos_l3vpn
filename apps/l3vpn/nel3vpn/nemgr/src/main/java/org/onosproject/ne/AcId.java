package org.onosproject.ne;

import static com.google.common.base.Preconditions.checkNotNull;
import org.onlab.util.Identifier;

public final class AcId extends Identifier<String> {
    // Public construction is prohibited
    private AcId(String acId) {
        super(checkNotNull(acId, "acId cannot be null"));
    }

    /**
     * Creates a Access identifier.
     *
     * @param acId the Access identifier in string
     * @return object of Access identifier
     */
    public static AcId of(String acId) {
        return new AcId(acId);
    }

    /**
     * Returns the Access identifier.
     *
     * @return the Access identifier
     */
    public String acId() {
        return identifier;
    }
}
