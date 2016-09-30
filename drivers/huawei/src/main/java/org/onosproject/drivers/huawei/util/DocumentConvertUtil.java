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
package org.onosproject.drivers.huawei.util;


import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.onosproject.drivers.huawei.HuaWeiL3vpnConfig.FilterType;
import org.onosproject.drivers.huawei.HuaWeiL3vpnConfig.NetconfConfigDatastoreType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * DocumentConvertUtil Document Convert Util.
 */
public final class DocumentConvertUtil {
    private static final Logger log = LoggerFactory
            .getLogger(DocumentConvertUtil.class);

    /**
     * Constructs a DataConvertUtil object. Utility classes should not have a
     * public or default constructor, otherwise IDE will compile unsuccessfully.
     * This class should not be instantiated.
     */
    private DocumentConvertUtil() {
    }

    /**
     * Convert To l3vpn Document.
     *
     * @param rpcXmlns rpc element attribute xmlns
     * @param datastoreType Netconf Config Datastore Type
     * @param errorOperation error operation
     * @param configXmlns l3vpn element attribute xmlns
     * @param netconfL3vpn NetconfL3vpn
     * @return Document
     */
    public static Document convertEditL3vpnDocument(String rpcXmlns,
                                                    NetconfConfigDatastoreType datastoreType,
                                                    String errorOperation,
                                                    String configXmlns,
                                                    JsonNode netconfL3vpn) {
        Document rpcDoc = convertRpcDocument(rpcXmlns);
        Document editDoc = convertEditConfigDocument(datastoreType,
                                                     errorOperation);
        Document l3vpnDoc = DocumentHelper.createDocument();
        Element l3vpn = l3vpnDoc.addElement("l3vpn");
        l3vpn.add(Namespace.get(configXmlns));
        l3vpn.addAttribute("content-version",
                           netconfL3vpn.path("contentVersion").asText());
        l3vpn.addAttribute("format-version",
                           netconfL3vpn.path("formatVersion").asText());
        Element l3vpncommon = l3vpn.addElement("l3vpncomm");
        Element l3vpnInstances = l3vpncommon.addElement("l3vpnInstances");
        for (JsonNode netconfL3vpnInstance : netconfL3vpn.path("l3vpnComm")
                .path("l3vpninstances")) {
            Element l3vpnInstance = l3vpnInstances.addElement("l3vpnInstance");
            l3vpnInstance.addAttribute("operation", netconfL3vpnInstance
                    .path("operation").asText());
            l3vpnInstance.addElement("vrfName")
                    .setText(netconfL3vpnInstance.path("vrfName").asText());
            Element vpnInstAFs = l3vpnInstance.addElement("vpnInstAFs");
            for (JsonNode netconfVpnInstAF : netconfL3vpnInstance
                    .path("vpnInstAFs")) {
                Element vpnInstAF = vpnInstAFs.addElement("vpnInstAF");
                vpnInstAF.addElement("afType")
                        .setText(netconfVpnInstAF.path("afType").asText());
                vpnInstAF.addElement("vrfRD")
                        .setText(netconfVpnInstAF.path("vrfRD").asText());
                Element vpnTargets = vpnInstAF.addElement("vpnTargets");
                for (JsonNode netconfVpnTarget : netconfVpnInstAF
                        .path("vpnTargets")) {
                    Element vpnTarget = vpnTargets.addElement("vpnTarget");
                    vpnTarget.addElement("vrfRTType").setText(netconfVpnTarget
                            .path("vrfRTType").asText());
                    vpnTarget.addElement("vrfRTValue").setText(netconfVpnTarget
                            .path("vrfRTValue").asText());
                }
            }
            Element l3vpnIfs = l3vpnInstance.addElement("l3vpnIfs");
            for (JsonNode netconfL3vpnIf : netconfL3vpnInstance
                    .path("l3vpnIfs")) {
                Element l3vpnIf = l3vpnIfs.addElement("l3vpnIf");
                l3vpnIf.addAttribute("operation",
                                     netconfL3vpnIf.path("operation").asText());
                l3vpnIf.addElement("ifName")
                        .setText(netconfL3vpnIf.path("ifName").asText());
                l3vpnIf.addElement("ipv4Addr")
                        .setText(netconfL3vpnIf.path("ipv4Addr").asText());
                l3vpnIf.addElement("subnetMask")
                        .setText(netconfL3vpnIf.path("subnetMask").asText());
            }
        }
        editDoc.getRootElement().element("config")
                .add(l3vpnDoc.getRootElement());
        rpcDoc.getRootElement().add(editDoc.getRootElement());
        return rpcDoc;
    }

    /**
     * Convert To bgp Document.
     *
     * @param rpcXmlns rpc element attribute xmlns
     * @param datastoreType Netconf Config Datastore Type
     * @param errorOperation error operation
     * @param configXmlns l3vpn element attribute xmlns
     * @param netconfBgp NetconfBgp
     * @return Document
     */
    public static Document convertEditBgpDocument(String rpcXmlns,
                                                  NetconfConfigDatastoreType datastoreType,
                                                  String errorOperation,
                                                  String configXmlns,
                                                  JsonNode netconfBgp) {
        Document rpcDoc = convertRpcDocument(rpcXmlns);
        Document editDoc = convertEditConfigDocument(datastoreType,
                                                     errorOperation);
        Document bgpDoc = DocumentHelper.createDocument();
        Element bgp = bgpDoc.addElement("bgp");
        bgp.add(Namespace.get(configXmlns));
        bgp.addAttribute("content-version",
                         netconfBgp.path("contentVersion").asText());
        bgp.addAttribute("format-version",
                         netconfBgp.path("formatVersion").asText());
        Element bgpcommon = bgp.addElement("bgpcomm");
        Element bgpVrfs = bgpcommon.addElement("bgpVrfs");

        for (JsonNode netconfBgpVrf : netconfBgp.path("bgpcomm")
                .path("bgpVrfs")) {
            Element bgpVrf = bgpVrfs.addElement("bgpVrf");
            bgpVrf.addAttribute("operation",
                                netconfBgpVrf.path("operation").asText());
            bgpVrf.addElement("vrfName")
                    .setText(netconfBgpVrf.path("vrfName").asText());
            Element bgpVrfAFs = bgpVrf.addElement("bgpVrfAFs");
            for (JsonNode netconfBgpVrfAF : netconfBgpVrf.path("bgpVrfAFs")) {
                Element bgpVrfAF = bgpVrfAFs.addElement("bgpVrfAF");
                bgpVrfAF.addElement("afType")
                        .setText(netconfBgpVrfAF.path("afType").asText());
                Element importRoutes = bgpVrfAF.addElement("importRoutes");
                for (JsonNode netconfImportRoute : netconfBgpVrfAF
                        .path("importRoutes")) {
                    Element importRoute = importRoutes
                            .addElement("importRoute");
                    importRoute.addAttribute("operation", netconfImportRoute
                            .path("operation").asText());
                    importRoute.addElement("importProtocol")
                            .setText(netconfImportRoute.path("importProtocol")
                                    .asText());
                    importRoute.addElement("importProcessId")
                            .setText(netconfImportRoute.path("importProcessId")
                                    .asText());
                }
            }
        }

        editDoc.getRootElement().element("config").add(bgpDoc.getRootElement());
        rpcDoc.getRootElement().add(editDoc.getRootElement());
        return rpcDoc;
    }

    /**
     * Convert To Rpc Document.
     *
     * @param xmlns xmlns
     * @return Document
     */
    public static Document convertRpcDocument(String xmlns) {
        Document doc = DocumentHelper.createDocument();
        Element rpc = doc.addElement("rpc");
        rpc.add(Namespace.get(xmlns));
        return doc;
    }

    /**
     * Convert To GET Document.
     *
     * @param type FilterType
     * @return Document
     */
    public static Document convertGetDocument(FilterType type) {
        Document doc = DocumentHelper.createDocument();
        Element get = doc.addElement("get");
        get.addElement("filter");
        get.addAttribute("type", type.name().toLowerCase());
        return doc;
    }

    /**
     * Convert To Edit Config Document.
     *
     * @param type Netconf Config Datastore Type
     * @param errorOperation error operation
     * @return Document
     */
    public static Document convertEditConfigDocument(NetconfConfigDatastoreType type,
                                                     String errorOperation) {
        Document doc = DocumentHelper.createDocument();
        Element editConfig = doc.addElement("edit-config");
        Element target = editConfig.addElement("target");
        target.addElement(type.name().toLowerCase());
        Element operation = editConfig.addElement("error-option");
        operation.setText(errorOperation);
        editConfig.addElement("config");
        return doc;
    }

}
