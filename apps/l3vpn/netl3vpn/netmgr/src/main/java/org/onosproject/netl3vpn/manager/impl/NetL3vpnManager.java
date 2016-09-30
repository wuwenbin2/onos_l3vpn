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
package org.onosproject.netl3vpn.manager.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import org.onlab.packet.IpAddress;
import org.onlab.util.KryoNamespace;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.incubator.net.resource.label.DefaultLabelResource;
import org.onosproject.incubator.net.resource.label.LabelResource;
import org.onosproject.incubator.net.resource.label.LabelResourceAdminService;
import org.onosproject.incubator.net.resource.label.LabelResourceId;
import org.onosproject.incubator.net.resource.label.LabelResourceService;
import org.onosproject.mastership.MastershipService;
import org.onosproject.ne.AcId;
import org.onosproject.ne.Bgp;
import org.onosproject.ne.BgpImportProtocol;
import org.onosproject.ne.BgpImportProtocol.ProtocolType;
import org.onosproject.ne.InstanceId;
import org.onosproject.ne.NeData;
import org.onosproject.ne.RouteDistinguisher;
import org.onosproject.ne.RouteTargets;
import org.onosproject.ne.VpnAc;
import org.onosproject.ne.VpnInstance;
import org.onosproject.ne.VrfEntity;
import org.onosproject.ne.manager.NeL3vpnService;
import org.onosproject.net.AnnotationKeys;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceService;
import org.onosproject.netl3vpn.entity.NetL3VpnAllocateRes;
import org.onosproject.netl3vpn.entity.WebAc;
import org.onosproject.netl3vpn.entity.WebNetL3vpnInstance;
import org.onosproject.netl3vpn.entity.WebNetL3vpnInstance.TopoModeType;
import org.onosproject.netl3vpn.manager.NetL3vpnService;
import org.onosproject.netl3vpn.util.ConvertUtil;
import org.onosproject.store.serializers.KryoNamespaces;
import org.onosproject.store.service.EventuallyConsistentMap;
import org.onosproject.store.service.LogicalClockService;
import org.onosproject.store.service.StorageService;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.acs.Ac;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.Instance;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.instance.nes.Ne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides implementation of NetL3vpnService.
 */
@Component(immediate = true)
@Service
public class NetL3vpnManager implements NetL3vpnService {
    private static final String INSTANCE_NOT_NULL = "Instance can not be null";
    private static final String APP_ID = "org.onosproject.app.l3vpn.net";
    private static final String RT = "rt";
    private static final String RD = "rd";
    private static final String VRF = "vrf";
    private static final String NETL3INSTANCESTORE = "netl3vpn-instance";
    private static final String NEL3INSTANCESTORE = "nel3vpn-instance";
    private static final String NEL3ACSTORE = "nel3vpn-ac";
    private static final String INSTANCESTORE = "instance";
    private static final long GLOBAL_LABEL_SPACE_MIN = 1;
    private static final long GLOBAL_LABEL_SPACE_MAX = Long.MAX_VALUE;
    private static final String RD_PREFIX = "100:";
    private static final String RT_PREFIX = "100:";
    private static final String VRF_PREFIX = "VRF_";
    protected static final Logger log = LoggerFactory
            .getLogger(NetL3vpnManager.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected MastershipService mastershipService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected StorageService storageService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LogicalClockService clockService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LabelResourceAdminService labelRsrcAdminService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LabelResourceService labelRsrcService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    private NeL3vpnService l3vpnNeService;

    private ApplicationId appId;
    private EventuallyConsistentMap<InstanceId, WebNetL3vpnInstance> webNetL3vpnStore;
    private EventuallyConsistentMap<DeviceId, VpnInstance> vpnInstanceStore;
    private EventuallyConsistentMap<AcId, VpnAc> vpnAcStore;
    private EventuallyConsistentMap<InstanceId, Instance> instanceStore;

    @Activate
    public void activate() {
        appId = coreService.registerApplication(APP_ID);
        KryoNamespace.Builder serializer = KryoNamespace.newBuilder()
                .register(KryoNamespaces.API).register(VpnInstance.class)
                .register(WebNetL3vpnInstance.class).register(VpnAc.class);
        webNetL3vpnStore = storageService
                .<InstanceId, WebNetL3vpnInstance>eventuallyConsistentMapBuilder()
                .withName(NETL3INSTANCESTORE).withSerializer(serializer)
                .withTimestampProvider((k, v) -> clockService.getTimestamp())
                .build();
        vpnInstanceStore = storageService
                .<DeviceId, VpnInstance>eventuallyConsistentMapBuilder()
                .withName(NEL3INSTANCESTORE).withSerializer(serializer)
                .withTimestampProvider((k, v) -> clockService.getTimestamp())
                .build();
        vpnAcStore = storageService
                .<AcId, VpnAc>eventuallyConsistentMapBuilder()
                .withName(NEL3ACSTORE).withSerializer(serializer)
                .withTimestampProvider((k, v) -> clockService.getTimestamp())
                .build();
        instanceStore = storageService
                .<InstanceId, Instance>eventuallyConsistentMapBuilder()
                .withName(INSTANCESTORE).withSerializer(serializer)
                .withTimestampProvider((k, v) -> clockService.getTimestamp())
                .build();

        if (!labelRsrcAdminService.createGlobalPool(LabelResourceId
                .labelResourceId(GLOBAL_LABEL_SPACE_MIN), LabelResourceId
                        .labelResourceId(GLOBAL_LABEL_SPACE_MAX))) {
            log.debug("Global node pool was already reserved.");
        }
        log.info("Started");
    }

    @Deactivate
    public void deactivate() {
        webNetL3vpnStore.clear();
        vpnInstanceStore.clear();
        vpnAcStore.clear();
        instanceStore.clear();
        log.info("Stopped");
    }

    @Override
    public boolean createL3vpn(Instance instance) {
        checkNotNull(instance, INSTANCE_NOT_NULL);
        if (!checkDeviceStatus(instance)) {
            return false;
        }
        WebNetL3vpnInstance webNetL3vpnInstance = null;
        webNetL3vpnInstance = cfgParse(instance);
        if (webNetL3vpnInstance == null) {
            log.debug("Parsing the web vpn instance failed, whose identifier is {} ",
                      instance.id());
            return false;
        }
        if (!checkOccupiedResource(webNetL3vpnInstance)) {
            log.debug("The resource of l3vpn instance is occupied.");
            return false;
        }
        NetL3VpnAllocateRes l3VpnAllocateRes = applyResource(webNetL3vpnInstance);
        if (l3VpnAllocateRes == null) {
            log.debug("Apply resources of l3vpn instance failed.");
            return false;
        }
        NeData neData = decompNeData(webNetL3vpnInstance, l3VpnAllocateRes);
        if (l3vpnNeService.createL3vpn(neData)) {
            webNetL3vpnStore.put(webNetL3vpnInstance.id(),
                                 webNetL3vpnInstance);
            for (VpnInstance vpnInstance : neData.vpnInstanceList()) {
                vpnInstanceStore.put(vpnInstance.neId(), vpnInstance);
            }
            for (VpnAc vpnAc : neData.vpnAcList()) {
                vpnAcStore.put(vpnAc.acId(), vpnAc);
            }
            instanceStore.put(InstanceId.of(instance.id()), instance);
            return true;
        }
        return false;
    }

    /**
     * Decompose the l3vpn network instance to network element data.
     *
     * @param webNetL3vpnInstance the l3vpn network instance
     * @return network element data
     */
    private NeData decompNeData(WebNetL3vpnInstance webNetL3vpnInstance,
                                NetL3VpnAllocateRes l3VpnAllocateRes) {
        List<VpnInstance> vpnInstanceList = new ArrayList<VpnInstance>();
        List<VpnAc> vpnAcList = new ArrayList<VpnAc>();

        Map<DeviceId, List<AcId>> acIdsByNeMap = new HashMap<DeviceId, List<AcId>>();
        Map<DeviceId, List<WebAc>> acsByNeMap = new HashMap<DeviceId, List<WebAc>>();
        for (WebAc webAc : webNetL3vpnInstance.acList()) {
            DeviceId neId = webAc.neId();
            List<AcId> acIdsByNeList = acIdsByNeMap.get(neId);
            if (acIdsByNeList == null) {
                acIdsByNeList = new ArrayList<AcId>();
            }
            acIdsByNeList.add(webAc.id());
            acIdsByNeMap.put(neId, acIdsByNeList);
            List<WebAc> acsByNeList = acsByNeMap.get(neId);
            if (acsByNeList == null) {
                acsByNeList = new ArrayList<WebAc>();
            }
            acsByNeList.add(webAc);
            acsByNeMap.put(neId, acsByNeList);
        }

        vpnInstanceList = decompVpnInstance(acIdsByNeMap, webNetL3vpnInstance,
                                            l3VpnAllocateRes);
        vpnAcList = decompVpnAc(acsByNeMap, webNetL3vpnInstance);
        return new NeData(vpnInstanceList, vpnAcList);
    }

    /**
     * Decompose the l3vpn network instance to list of vpn instance.
     *
     * @param acIdsByNeMap a map of ac ids to each ne
     * @param webNetL3vpnInstance the l3vpn network instance
     * @return list of vpn instance
     */
    private List<VpnInstance> decompVpnInstance(Map<DeviceId, List<AcId>>  acIdsByNeMap,
                                                WebNetL3vpnInstance webNetL3vpnInstance,
                                                NetL3VpnAllocateRes l3VpnAllocateRes) {
        List<VpnInstance> vpnInstanceList = new ArrayList<VpnInstance>();
        for (DeviceId neId : webNetL3vpnInstance.neIdList()) {
            String vrfName = l3VpnAllocateRes.vrfName();
            InstanceId netVpnId = webNetL3vpnInstance.id();
            RouteDistinguisher routeDistinguisher = l3VpnAllocateRes
                    .routeDistinguisherMap().get(neId);
            List<RouteTargets> importTargets = l3VpnAllocateRes.routeTargets();
            List<RouteTargets> exportTargets = l3VpnAllocateRes.routeTargets();
            List<AcId> acIdList = acIdsByNeMap.get(neId);

            BgpImportProtocol bgpImportProtocol = new BgpImportProtocol(ProtocolType.DIRECT,
                                                                        "0");
            List<BgpImportProtocol> importProtocols = new ArrayList<BgpImportProtocol>();
            importProtocols.add(bgpImportProtocol);
            Bgp bgp = new Bgp(importProtocols);

            VrfEntity vrfEntity = new VrfEntity(vrfName, netVpnId,
                                                routeDistinguisher,
                                                importTargets, exportTargets,
                                                acIdList, bgp);
            List<VrfEntity> vrfList = new ArrayList<VrfEntity>();
            vrfList.add(vrfEntity);
            VpnInstance vpnInstance = new VpnInstance(neId, vrfList);
            vpnInstanceList.add(vpnInstance);
        }
        return vpnInstanceList;
    }

    /**
     * Decompose the l3vpn network instance to list of vpn ac.
     *
     * @param acsByNeMap a map of ac entities to each ne
     * @param webNetL3vpnInstance the l3vpn network instance
     * @return list of vpn ac
     */
    private List<VpnAc> decompVpnAc(Map<DeviceId, List<WebAc>> acsByNeMap,
                                    WebNetL3vpnInstance webNetL3vpnInstance) {
        List<VpnAc> vpnAcList = new ArrayList<VpnAc>();
        InstanceId netVpnId = webNetL3vpnInstance.id();
        for (DeviceId neId : webNetL3vpnInstance.neIdList()) {
            List<WebAc> webAcList = acsByNeMap.get(neId);
            for (WebAc webAc : webAcList) {
                AcId acId = webAc.id();
                Port port = deviceService
                        .getPort(neId,
                                 PortNumber.portNumber(webAc.l2Access()
                                         .port().ltpId()));
                String acName = port.annotations()
                        .value(AnnotationKeys.PORT_NAME);
                IpAddress ipAddress = IpAddress.valueOf(webAc.l3Access().address().split("/")[0]);
                int subNetMask = Integer.parseInt(webAc.l3Access()
                        .address().split("/")[1]);
                VpnAc vpnAc = new VpnAc(netVpnId, acId, acName, ipAddress,
                                        subNetMask);
                vpnAcList.add(vpnAc);
            }
        }
        return vpnAcList;
    }

    private WebNetL3vpnInstance cfgParse(Instance instance) {
        List<DeviceId> neIdList = new ArrayList<DeviceId>();
        for (Ne ne : instance.nes().ne()) {
            neIdList.add(DeviceId.deviceId(ne.id()));
        }
        List<WebAc> webAcs = new ArrayList<WebAc>();
        for (Ac ac : instance.acs().ac()) {
            webAcs.add(ConvertUtil.convertToWebAc(ac));
        }
        WebNetL3vpnInstance webNetL3vpnInstance = new WebNetL3vpnInstance(InstanceId
                .of(instance.id()), instance.name(), TopoModeType
                        .valueOf("FullMesh"), neIdList, webAcs);
        return webNetL3vpnInstance;
    }

    /**
     * Check the status of devices for the instance.
     *
     * @param instance the specific instance
     * @return success or failure
     */
    private boolean checkDeviceStatus(Instance instance) {
        for (Ne ne : instance.nes().ne()) {
            DeviceId deviceId = DeviceId.deviceId(ne.id());
            if (deviceService.getDevice(deviceId) == null) {
                log.debug("Cannot get the device, whose ne id is {}.", ne.id());
                return false;
            }
            if (!deviceService.isAvailable(deviceId)) {
                log.debug("The device whose ne id is {} cannot be available.",
                          ne.id());
                return false;
            }
            if (!mastershipService.isLocalMaster(deviceId)) {
                log.debug("The device whose ne id is {} is not master role.",
                          ne.id());
                return false;
            }
        }
        return true;
    }

    /**
     * Check the resource is valid or not.
     *
     * @return valid or not
     */
    private boolean checkOccupiedResource(WebNetL3vpnInstance webNetL3vpnInstance) {
        for (Entry<InstanceId, WebNetL3vpnInstance> entry : webNetL3vpnStore
                .entrySet()) {
            if ((entry.getKey() == webNetL3vpnInstance.id()) || (entry
                    .getValue().name() == webNetL3vpnInstance.name())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Apply the node labels from global node label pool.
     *
     * @return the allocate resource entity
     */
    private NetL3VpnAllocateRes applyResource(WebNetL3vpnInstance webNetL3vpnInstance) {
        RouteTargets routeTarget = RouteTargets.of(allocateResource(RT, webNetL3vpnInstance));
        Map<DeviceId, RouteDistinguisher> routeDistinguisherMap = new HashMap<DeviceId, RouteDistinguisher>();
        for (DeviceId neId : webNetL3vpnInstance.neIdList()) {
            RouteDistinguisher routeDistinguisher = RouteDistinguisher.of(allocateResource(RD,
                                                         webNetL3vpnInstance));
            routeDistinguisherMap.put(neId, routeDistinguisher);
        }

        String vrfName = allocateResource(VRF, webNetL3vpnInstance);
        if (routeTarget == null || routeDistinguisherMap == null
                || vrfName == null) {
            return null;
        }

        List<RouteTargets> routeTargets = new ArrayList<RouteTargets>();
        routeTargets.add(routeTarget);
        return new NetL3VpnAllocateRes(routeTargets, routeDistinguisherMap,
                                       vrfName);
    }

    private String allocateResource(String allocateType,
                                    WebNetL3vpnInstance webNetL3vpnInstance) {
        long applyNum = 1; // For each vpn only one rd label
        LabelResourceId specificLabelId = null;
        Collection<LabelResource> result = labelRsrcService
                .applyFromGlobalPool(applyNum);
        if (result.size() > 0) {
            // Only one element to retrieve
            Iterator<LabelResource> iterator = result.iterator();
            DefaultLabelResource defaultLabelResource = (DefaultLabelResource) iterator
                    .next();
            specificLabelId = defaultLabelResource.labelResourceId();
            if (specificLabelId == null) {
                log.error("Unable to retrieve {} label for a vpn id {}.",
                          allocateType, webNetL3vpnInstance.id());
                return null;
            }
        } else {
            log.error("Unable to allocate {} label for a vpn id {}.",
                      allocateType, webNetL3vpnInstance.id());
            return null;
        }
        switch (allocateType) {
        case "rd":
            return RD_PREFIX + specificLabelId.id();
        case "rt":
            return RT_PREFIX + specificLabelId.id();
        case "vrf":
            return VRF_PREFIX + specificLabelId.id();
        default:
            log.error("Unable to allocate {} label for a vpn id {}.",
                      allocateType, webNetL3vpnInstance.id());
            return null;
        }
    }

    @Override
    public Collection<Instance> getInstances() {
        return instanceStore.values();
    }

    @Override
    public Instance getInstance(InstanceId instanceId) {
        return instanceStore.get(instanceId);
    }

}
