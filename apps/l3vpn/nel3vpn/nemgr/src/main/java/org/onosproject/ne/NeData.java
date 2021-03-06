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
package org.onosproject.ne;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

/**
 * NeData network element data.
 */
public final class NeData {
    private final List<VpnInstance> vpnInstanceList;
    private final List<VpnAc> vpnAcList;

    /**
     * NeData constructor.
     *
     * @param vpnInstanceList list of VpnInstance
     * @param vpnAcList list of VpnAc
     */
    public NeData(List<VpnInstance> vpnInstanceList, List<VpnAc> vpnAcList) {
        checkNotNull(vpnInstanceList, "vpnInstanceList cannot be null");
        checkNotNull(vpnAcList, "vpnAcList cannot be null");
        this.vpnInstanceList = vpnInstanceList;
        this.vpnAcList = vpnAcList;
    }

    /**
     * Returns vpnInstanceList.
     *
     * @return vpnInstanceList
     */
    public List<VpnInstance> vpnInstanceList() {
        return vpnInstanceList;
    }

    /**
     * Returns vpnAcList.
     *
     * @return vpnAcList
     */
    public List<VpnAc> vpnAcList() {
        return vpnAcList;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vpnInstanceList, vpnAcList);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NeData) {
            final NeData other = (NeData) obj;
            return Objects.equals(this.vpnInstanceList, other.vpnInstanceList)
                    && Objects.equals(this.vpnAcList, other.vpnAcList);
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(this).add("vpnInstanceList", vpnInstanceList)
                .add("vpnAcList", vpnAcList).toString();
    }
}
