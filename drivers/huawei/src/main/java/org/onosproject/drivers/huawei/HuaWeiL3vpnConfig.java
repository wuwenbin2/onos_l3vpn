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
package org.onosproject.drivers.huawei;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import org.dom4j.Document;
import org.onosproject.drivers.huawei.util.DocumentConvertUtil;
import org.onosproject.net.DeviceId;
import org.onosproject.net.behaviour.L3vpnConfig;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.netconf.NetconfController;
import org.onosproject.netconf.NetconfException;
import org.onosproject.netconf.NetconfSession;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Configures l3vpn on HuaWei devices.
 */
public class HuaWeiL3vpnConfig extends AbstractHandlerBehaviour
        implements L3vpnConfig {
    private final Logger log = getLogger(getClass());
    private static final String RPC_XMLNS = "urn:ietf:params:xml:ns:netconf:base:1.0";
    private static final String CONFIG_XMLNS = "http://www.huawei.com/netconf/vrp";
    private static final String ERROR_OPERATION = "rollback-on-error";
    private static final String DEFAULT_NAMESPACE = " xmlns=\"\"";

    @Override
    public boolean createVrf(DeviceId deviceId, JsonNode netconfL3vpn) {
        NetconfController controller = checkNotNull(handler()
                .get(NetconfController.class));
        NetconfSession session = controller.getDevicesMap().get(deviceId)
                .getSession();
        Document l3vpnDocument = DocumentConvertUtil
                .convertEditL3vpnDocument(RPC_XMLNS,
                                          NetconfConfigDatastoreType.RUNNING,
                                          ERROR_OPERATION, CONFIG_XMLNS,
                                          netconfL3vpn);
        String requestMessage = l3vpnDocument.asXML()
                .replaceAll(DEFAULT_NAMESPACE, "");
        log.debug("Vrf requestMessage is :{}", requestMessage);
        boolean reply = false;
        try {
            reply = session.editConfig(requestMessage);
        } catch (NetconfException e) {
            log.error("Failed to create virtual routing forwarding. Caused by:{}",
                      e);
        }
        return reply;
    }

    @Override
    public boolean createBgpImportProtocol(DeviceId deviceId,
                                           JsonNode netconfBgp) {
        NetconfController controller = checkNotNull(handler()
                .get(NetconfController.class));
        NetconfSession session = controller.getDevicesMap().get(deviceId)
                .getSession();
        Document bgpDocument = DocumentConvertUtil
                .convertEditBgpDocument(RPC_XMLNS,
                                        NetconfConfigDatastoreType.RUNNING,
                                        ERROR_OPERATION, CONFIG_XMLNS,
                                        netconfBgp);
        String requestMessage = bgpDocument.asXML()
                .replaceAll(DEFAULT_NAMESPACE, "");
        log.debug("Bgp requestMessage is :{}", requestMessage);
        boolean reply = false;
        try {
            reply = session.editConfig(requestMessage);
        } catch (NetconfException e) {
            log.error("Failed to create bgp import protocol. Caused by:{}", e);
        }
        return reply;
    }

    /**
     * The enumeration of Netconf Config Datastore type.
     */
    public enum NetconfConfigDatastoreType {
        STARTUP(1), RUNNING(2), CANDIDATE(3), UNKNOWN(-1);

        int value;

        private NetconfConfigDatastoreType(int value) {
            this.value = value;
        }

        public int value() {
            return value;
        }
    }

    /**
     * The enumeration of Filter type.
     */
    public enum FilterType {
        SUBTREE, XPATH, OTHERS;
    }

}
