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
package org.onosproject.nel3vpnweb.codec;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.ArrayList;
import java.util.List;
import org.onlab.packet.IpAddress;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.ne.AcId;
import org.onosproject.ne.Bgp;
import org.onosproject.ne.BgpImportProtocol;
import org.onosproject.ne.BgpImportProtocol.ProtocolType;
import org.onosproject.ne.NeData;
import org.onosproject.ne.RouteDistinguisher;
import org.onosproject.ne.RouteTargets;
import org.onosproject.ne.VpnAc;
import org.onosproject.ne.VpnInstance;
import org.onosproject.ne.VrfEntity;
import org.onosproject.net.DeviceId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * NeData JSON codec.
 */
public final class NeDataCodec extends JsonCodec<NeData> {

    public static final String OBJECTNODE_NOT_NULL = "ObjectNode can not be null";
    public static final String CODECCONTEXT_NOT_NULL = "CodecContext can not be null";

    private static final String DIRECT = "direct";
    private static final String BGP = "bgp";
    private static final String ISIS = "isis";
    private static final String OSPF = "ospf";

    @Override
    public ObjectNode encode(NeData nedata, CodecContext context) {
        checkNotNull(nedata, "nedata cannot be null");
        Iterable<VpnInstance> vpnInstances = nedata.vpnInstanceList();
        Iterable<VpnAc> vpnAcs = nedata.vpnAcList();
        ObjectNode result = context.mapper().createObjectNode();
        result.set("instances",
                   new VpnInstanceCodec().encode(vpnInstances, context));
        result.set("acs", new VpnAcCodec().encode(vpnAcs, context));
        return result;
    }

    @Override
    public NeData decode(ObjectNode json, CodecContext context) {
        checkNotNull(json, OBJECTNODE_NOT_NULL);
        checkNotNull(context, CODECCONTEXT_NOT_NULL);
        JsonNode networkelement = json.get("networkelement");
        JsonNode instances = networkelement.get("instances");
        JsonNode acs = networkelement.get("acs");
        List<VpnInstance> vpnInstanceList = new ArrayList<VpnInstance>();
        List<VpnAc> vpnAcList = new ArrayList<VpnAc>();
        for (JsonNode instance : instances) {
            DeviceId neid = DeviceId.deviceId(instance.get("neid").asText());
            JsonNode vrfs = instance.get("vrfs");
            List<VrfEntity> vrfList = new ArrayList<VrfEntity>();
            for (JsonNode vrf : vrfs) {
                String name = vrf.get("name").asText();
                RouteDistinguisher rd = RouteDistinguisher.of(vrf.get("rd").asText());
                List<RouteTargets> its = new ArrayList<RouteTargets>();
                for (JsonNode it : vrf.get("its")) {
                    its.add(RouteTargets.of(it.asText()));
                }
                List<RouteTargets> ets = new ArrayList<RouteTargets>();
                for (JsonNode et : vrf.get("ets")) {
                    ets.add(RouteTargets.of(et.asText()));
                }
                List<AcId> acids = new ArrayList<AcId>();
                for (JsonNode acid : vrf.get("acids")) {
                    acids.add(AcId.of(acid.asText()));
                }
                JsonNode importroutes = vrf.get("bgp").get("importroutes");
                List<BgpImportProtocol> importProtocols = new ArrayList<BgpImportProtocol>();
                for (JsonNode importroute : importroutes) {
                    String processId = importroute.get("importprocessid")
                            .asText();
                    switch (importroute.get("importprotocol").asText()
                            .toLowerCase()) {
                    case DIRECT:
                        importProtocols
                                .add(new BgpImportProtocol(ProtocolType.DIRECT,
                                                           processId));
                        break;
                    case BGP:
                        importProtocols
                                .add(new BgpImportProtocol(ProtocolType.BGP,
                                                           processId));
                        break;
                    case ISIS:
                        importProtocols
                                .add(new BgpImportProtocol(ProtocolType.ISIS,
                                                           processId));
                        break;
                    case OSPF:
                        importProtocols
                                .add(new BgpImportProtocol(ProtocolType.OSPF,
                                                           processId));
                        break;
                    default:
                        break;
                    }
                }
                Bgp bgp = new Bgp(importProtocols);
                vrfList.add(new VrfEntity(name, rd, its, ets, acids, bgp));
            }
            vpnInstanceList.add(new VpnInstance(neid, vrfList));
        }
        for (JsonNode ac : acs) {
            AcId id = AcId.of(ac.get("id").asText());
            String name = ac.get("name").asText();
            IpAddress ip = IpAddress.valueOf(ac.get("ip").asText());
            Integer mask = ac.get("mask").asInt();
            vpnAcList.add(new VpnAc(id, name, ip, mask));
        }
        return new NeData(vpnInstanceList, vpnAcList);
    }
}
