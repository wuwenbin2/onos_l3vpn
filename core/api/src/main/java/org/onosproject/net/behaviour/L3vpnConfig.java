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
package org.onosproject.net.behaviour;

import org.onosproject.net.DeviceId;
import org.onosproject.net.driver.HandlerBehaviour;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Behaviour for handling various drivers for l3vpn configurations.
 */
public interface L3vpnConfig extends HandlerBehaviour {

    /**
     * Create virtual routing forwarding.
     *
     * @param deviceId device identifier
     * @param JsonNode NetconfL3vpn jsonNode
     * @return true or false
     */
    boolean createVrf(DeviceId deviceId, JsonNode jsonNode);

    /**
     * Create Bgp Import Protocol.
     *
     * @param deviceId device identifier
     * @param JsonNode NetconfBgp jsonNode
     * @return true or false
     */
    boolean createBgpImportProtocol(DeviceId deviceId, JsonNode jsonNode);
}
