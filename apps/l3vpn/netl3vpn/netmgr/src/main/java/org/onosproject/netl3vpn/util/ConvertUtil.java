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
package org.onosproject.netl3vpn.util;

import org.onosproject.ne.AcId;
import org.onosproject.net.DeviceId;
import org.onosproject.netl3vpn.entity.WebAc;
import org.onosproject.netl3vpn.entity.WebL2Access;
import org.onosproject.netl3vpn.entity.WebL2Access.L2AccessType;
import org.onosproject.netl3vpn.entity.WebL3Access;
import org.onosproject.netl3vpn.entity.WebPort;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.acs.Ac;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.acs.ac.L2Access;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.acgroup.acs.ac.L3Access;
import org.onosproject.yang.gen.v1.net.l3vpn.type.rev20160701.netl3vpntype.l2access.Port;

/**
 * Convert utility class.
 */
public final class ConvertUtil {

    /**
     * Initializes default values.
     */
    private ConvertUtil() {
    }

    /**
     * Hanle the String value to specific format.
     *
     * @param strValue String value
     * @return converted value
     */
    public static String handleStringValue(String strValue) {
        StringBuffer strBuf = new StringBuffer();
        if (strValue != null && !strValue.equals("")) {
            if (strValue.contains("-")) {
                String[] enumValues = strValue.split("-");
                for (String str : enumValues) {
                    char ch = str.charAt(0);
                    strBuf.append(Character.toUpperCase(ch));
                    strBuf.append(str.subSequence(1, str.length()));
                }
            } else {
                char ch = strValue.charAt(0);
                strBuf.append(Character.toUpperCase(ch));
                strBuf.append(strValue.subSequence(1, strValue.length()));
            }
            return strBuf.toString();
        }
        return null;
    }

    /**
     * Convert Ac to WebAc.
     *
     * @param ac ac
     * @return WebAc
     */
    public static WebAc convertToWebAc(Ac ac) {
        return new WebAc(AcId.of(ac.id()), DeviceId.deviceId(ac.neId()),
                         convertToWebL2Access(ac.l2Access()),
                         convertToWebL3Access(ac.l3Access()));
    }

    /**
     * Convert L2Access to WebL2Access.
     *
     * @param l2Access l2 access
     * @return WebL2Access
     */
    public static WebL2Access convertToWebL2Access(L2Access l2Access) {
        return new WebL2Access(L2AccessType
                .valueOf(handleStringValue(l2Access.accessType())),
                               convertToWebPort(l2Access.port()));
    }

    /**
     * Convert Port to WebPort.
     *
     * @param port port
     * @return WebPort
     */
    public static WebPort convertToWebPort(Port port) {
        return new WebPort(handleStringValue(port.ltpId()));
    }

    /**
     * Convert L3Access to WebL3Access.
     *
     * @param l3Access l3 Access
     * @return WebL3Access
     */
    public static WebL3Access convertToWebL3Access(L3Access l3Access) {
        return new WebL3Access(l3Access.address().string());
    }
}
