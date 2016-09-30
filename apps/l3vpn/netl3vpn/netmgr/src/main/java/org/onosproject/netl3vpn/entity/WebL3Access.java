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
 * Immutable representation of a web l3 access.
 */
public final class WebL3Access {
    private final String address;

    /**
     * WebL3Access constructor.
     *
     * @param address ip address
     */
    public WebL3Access(String address) {
        checkNotNull(address, "address cannot be null");
        this.address = address;
    }

    /**
     * Returns the WebL3Access address.
     *
     * @return address
     */
    public String address() {
        return address;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof WebL3Access) {
            final WebL3Access webL3Access = (WebL3Access) obj;
            return Objects.equals(this.address, webL3Access.address());
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(this).add("address", this.address).toString();
    }
}
