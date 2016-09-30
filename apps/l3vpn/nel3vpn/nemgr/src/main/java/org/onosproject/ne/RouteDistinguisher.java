package org.onosproject.ne;

import static com.google.common.base.Preconditions.checkNotNull;

import org.onlab.util.Identifier;

public final class RouteDistinguisher extends Identifier<String> {
    // Public construction is prohibited
    private RouteDistinguisher(String routeDistinguisher) {
        super(checkNotNull(routeDistinguisher, "routeDistinguisher cannot be null"));
    }

    /**
     * Creates a RouteDistinguisher identifier.
     *
     * @param routeTargets the RouteDistinguisher identifier in string
     * @return object of RouteDistinguisher identifier
     */
    public static RouteDistinguisher of(String routeDistinguisher) {
        return new RouteDistinguisher(routeDistinguisher);
    }

    /**
     * Returns the RouteDistinguisher identifier.
     *
     * @return the RouteDistinguisher identifier
     */
    public String routeDistinguisher() {
        return identifier;
    }
}
