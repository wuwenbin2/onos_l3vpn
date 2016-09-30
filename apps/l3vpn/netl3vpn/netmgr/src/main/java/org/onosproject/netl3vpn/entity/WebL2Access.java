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

import java.util.Objects;

/**
 * Immutable representation of a web l2 access.
 */
public final class WebL2Access {
    private final L2AccessType accessType;
    private final WebPort port;

    /**
     * WebL2Access constructor.
     *
     * @param accessType access type
     * @param port port
     */
    public WebL2Access(L2AccessType accessType, WebPort port) {
        checkNotNull(accessType, "accessType cannot be null");
        checkNotNull(port, "port cannot be null");
        this.accessType = accessType;
        this.port = port;
    }

    /**
     * The enumeration of l2 access type.
     */
    public enum L2AccessType {
        Untag(0), Port(1), Dot1q(2), Qing(3), Transport(4), Vxlan(5);

        int value;

        private L2AccessType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    /**
     * Returns the WebL2Access address type.
     *
     * @return l2 access type
     */
    public L2AccessType accessType() {
        return accessType;
    }

    /**
     * Returns the WebL2Access port.
     *
     * @return port
     */
    public WebPort port() {
        return port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(port, accessType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof WebL2Access) {
            final WebL2Access webL2Access = (WebL2Access) obj;
            return Objects.equals(this.port, webL2Access.port())
                    && Objects.equals(this.accessType, webL2Access.accessType);
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("port", this.port.ltpId())
                .add("accessType", String.valueOf(this.accessType().value)).toString();
    }
}
