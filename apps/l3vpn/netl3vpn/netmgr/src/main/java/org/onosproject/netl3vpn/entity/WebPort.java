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
 * Immutable representation of a web port.
 */
public final class WebPort {
    private final String ltpId;

    /**
     * WebPort constructor.
     *
     * @param accessType access type
     * @param port port
     */
    public WebPort(String ltpId) {
        checkNotNull(ltpId, "ltpId cannot be null");
        this.ltpId = ltpId;
    }

    /**
     * Returns the WebPort ltp id.
     *
     * @return ltp id
     */
    public String ltpId() {
        return ltpId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ltpId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof WebPort) {
            final WebPort webPort = (WebPort) obj;
            return Objects.equals(this.ltpId, webPort.ltpId);
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("ltpId", this.ltpId).toString();
    }
}
