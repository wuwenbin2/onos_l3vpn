/*
 * Copyright 2015-present Open Networking Laboratory
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
package org.onosproject.net.device;

import org.onosproject.net.DeviceId;
import org.onosproject.net.config.Config;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Configuration object for l3 vpn vrf.
 */
public class L3vpnVrfConfig extends Config<DeviceId> {

    private static final String CONTENTVERDION = "contentVersion";

    @Override
    public boolean isValid() {
        return get(CONTENTVERDION, null) != null;
    }

    public L3vpnVrfConfig setNode(JsonNode node) {
        this.node = node;
        return this;
    }

}
