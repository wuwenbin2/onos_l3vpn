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
package org.onosproject.provider.netconf.device.impl;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.DeviceId;
import org.onosproject.net.behaviour.L3vpnConfig;
import org.onosproject.net.config.ConfigFactory;
import org.onosproject.net.config.NetworkConfigEvent;
import org.onosproject.net.config.NetworkConfigListener;
import org.onosproject.net.config.NetworkConfigRegistry;
import org.onosproject.net.config.NetworkConfigService;
import org.onosproject.net.config.basics.SubjectFactories;
import org.onosproject.net.device.L3vpnBgpConfig;
import org.onosproject.net.device.L3vpnVrfConfig;
import org.onosproject.net.driver.DriverHandler;
import org.onosproject.net.driver.DriverService;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.slf4j.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Netconfg L3vpn config provider to validate and populate the configuration.
 */
@Component(immediate = true)
public class NetconfVpnProvider extends AbstractProvider {

    private static final Logger log = getLogger(NetconfVpnProvider.class);

    static final String PROVIDER_ID = "org.onosproject.provider.netconf.l3vpn.cfg";
    static final String NETCONFL3VPN = "netconfL3vpn";
    static final String NETCONFBGP = "netconfBgp";

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected NetworkConfigRegistry configRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected NetworkConfigService configService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DriverService driverService;

    private final ConfigFactory<DeviceId, L3vpnVrfConfig> vrfconfigFactory =
            new ConfigFactory<DeviceId, L3vpnVrfConfig>(SubjectFactories.DEVICE_SUBJECT_FACTORY,
                    L3vpnVrfConfig.class,
                    NETCONFL3VPN) {
        @Override
        public L3vpnVrfConfig createConfig() {
            return new L3vpnVrfConfig();
        }
    };

    private final ConfigFactory<DeviceId, L3vpnBgpConfig> bgpconfigFactory =
            new ConfigFactory<DeviceId, L3vpnBgpConfig>(SubjectFactories.DEVICE_SUBJECT_FACTORY,
                    L3vpnBgpConfig.class,
                    NETCONFBGP) {
        @Override
        public L3vpnBgpConfig createConfig() {
            return new L3vpnBgpConfig();
        }
    };

    private final NetworkConfigListener configListener = new InternalConfigListener();

    private ApplicationId appId;

    /**
     * Creates a l3 config provider.
     */
    public NetconfVpnProvider() {
        super(new ProviderId("l3vpn", PROVIDER_ID));
    }

    @Activate
    public void activate() {
        appId = coreService.registerApplication(PROVIDER_ID);
        configService.addListener(configListener);
        configRegistry.registerConfigFactory(vrfconfigFactory);
        configRegistry.registerConfigFactory(bgpconfigFactory);
        log.info("l3vpn cfg provider started");
    }

    @Deactivate
    public void deactivate() {
        configRegistry.unregisterConfigFactory(vrfconfigFactory);
        configRegistry.unregisterConfigFactory(bgpconfigFactory);
        configService.removeListener(configListener);
    }

    /**
     * L3 vpn config listener to populate the configuration.
     */
    private class InternalConfigListener implements NetworkConfigListener {

        @Override
        public void event(NetworkConfigEvent event) {
            if (!isRelevant(event)) {
                log.info("NetworkConfigEvent is not relevant");
                return;
            }

            DeviceId deviceId = (DeviceId) event.subject();
            if (event.configClass().equals(L3vpnVrfConfig.class)) {
                L3vpnVrfConfig l3vpnVrfConfig = configService
                        .getConfig(deviceId, L3vpnVrfConfig.class);
                if (l3vpnVrfConfig.isValid()) {
                    JsonNode netconfL3vpn = l3vpnVrfConfig.node();
                    switch (event.type()) {
                    case CONFIG_ADDED:
                        log.info("netconf l3 vpn cfg added and do nothing");
                        break;
                    case CONFIG_UPDATED:
                        log.info("netconf l3 vpn cfg updated");
                        netconfL3vpn(deviceId, netconfL3vpn);
                        break;
                    case CONFIG_REMOVED:
                    default:
                        break;
                    }
                }
            }
            if (event.configClass().equals(L3vpnBgpConfig.class)) {
                L3vpnBgpConfig l3vpnBgpConfig = configService
                        .getConfig(deviceId, L3vpnBgpConfig.class);
                if (l3vpnBgpConfig.isValid()) {
                    JsonNode netconfBgp = l3vpnBgpConfig.node();
                    switch (event.type()) {
                    case CONFIG_ADDED:
                        log.info("netconf bpg cfg added and do nothing");
                        break;
                    case CONFIG_UPDATED:
                        log.info("netconf bpg cfg updated");
                        netconfBgp(deviceId, netconfBgp);
                        break;
                    case CONFIG_REMOVED:
                    default:
                        break;
                    }
                }
            }
        }

        @Override
        public boolean isRelevant(NetworkConfigEvent event) {
            return (event.configClass().equals(L3vpnVrfConfig.class)
                    || event.configClass().equals(L3vpnBgpConfig.class))
                    && (event.type() == NetworkConfigEvent.Type.CONFIG_ADDED
                            || event.type() == NetworkConfigEvent.Type.CONFIG_UPDATED);
        }

    }

    public void netconfL3vpn(DeviceId deviceId, JsonNode netconfL3vpn) {
        DriverHandler handler = driverService.createHandler(deviceId);
        L3vpnConfig l3vpnConfig = handler.behaviour(L3vpnConfig.class);
        boolean result = l3vpnConfig.createVrf(deviceId, netconfL3vpn);
        if (result) {
            log.info("L3 vpn vrf is configurated successfully");
        } else {
            log.info("L3 vpn vrf isn't configurated successfully");
        }
    }

    public void netconfBgp(DeviceId deviceId, JsonNode netconfBgp) {
        DriverHandler handler = driverService.createHandler(deviceId);
        L3vpnConfig l3vpnConfig = handler.behaviour(L3vpnConfig.class);
        boolean result = l3vpnConfig.createBgpImportProtocol(deviceId,
                                                             netconfBgp);
        if (result) {
            log.info("L3 vpn bgp is configurated successfully");
        } else {
            log.info("L3 vpn bgp isn't configurated successfully");
        }

    }
}
