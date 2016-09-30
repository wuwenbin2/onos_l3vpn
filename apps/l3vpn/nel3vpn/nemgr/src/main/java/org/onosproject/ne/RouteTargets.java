package org.onosproject.ne;

import static com.google.common.base.Preconditions.checkNotNull;

import org.onlab.util.Identifier;

public final class RouteTargets extends Identifier<String> {
    // Public construction is prohibited
    private RouteTargets(String routeTargets) {
        super(checkNotNull(routeTargets, "routeTargets cannot be null"));
    }

    /**
     * Creates a RouteTargets identifier.
     *
     * @param routeTargets the RouteTargets identifier in string
     * @return object of RouteTargets identifier
     */
    public static RouteTargets of(String routeTargets) {
        return new RouteTargets(routeTargets);
    }

    /**
     * Returns the RouteTargets identifier.
     *
     * @return the RouteTargets identifier
     */
    public String routeTargets() {
        return identifier;
    }
}
