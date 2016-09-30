/*
 * Copyright 2016-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.netl3vpn.entity;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.onosproject.ne.RouteDistinguisher;
import org.onosproject.ne.RouteTargets;
import org.onosproject.net.DeviceId;

/**
 * Resource allocate class.
 */
public final class NetL3VpnAllocateRes {
    private final List<RouteTargets> routeTargets;
    private final Map<DeviceId, RouteDistinguisher> routeDistinguisherMap;
    private final String vrfName;

    /**
     * NetL3VpnAllocateRes constructor.
     *
     * @param routeTargets
     * @param routeDistinguisher
     * @param vrfName
     */
    public NetL3VpnAllocateRes(List<RouteTargets> routeTargets,
                               Map<DeviceId, RouteDistinguisher> routeDistinguisherMap,
                               String vrfName) {
        checkNotNull(routeTargets, "routeTargets is not null");
        checkNotNull(routeDistinguisherMap,
                     "routeDistinguisherMap is not null");
        checkNotNull(vrfName, "vrfName is not null");
        this.routeTargets = routeTargets;
        this.routeDistinguisherMap = routeDistinguisherMap;
        this.vrfName = vrfName;
    }

    /**
     * Returns the route targets.
     *
     * @return list of route target
     */
    public List<RouteTargets> routeTargets() {
        return routeTargets;
    }

    /**
     * Returns the map of route distinguisher.
     *
     * @return map of route distinguisher
     */
    public Map<DeviceId, RouteDistinguisher> routeDistinguisherMap() {
        return routeDistinguisherMap;
    }

    /**
     * Returns the vrf name.
     *
     * @return vrf name
     */
    public String vrfName() {
        return vrfName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(routeTargets, routeDistinguisherMap, vrfName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NetL3VpnAllocateRes) {
            final NetL3VpnAllocateRes netL3VpnAllocateRes = (NetL3VpnAllocateRes) obj;
            return Objects.equals(this.routeTargets,
                                  netL3VpnAllocateRes.routeTargets)
                    && Objects.equals(this.routeDistinguisherMap,
                                      netL3VpnAllocateRes.routeDistinguisherMap)
                    && Objects.equals(this.vrfName,
                                      netL3VpnAllocateRes.vrfName);
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(this).add("routeTargets", routeTargets.toString())
                .add("routeDistinguisherMap", routeDistinguisherMap.toString())
                .add("vrfName", vrfName).toString();
    }
}
