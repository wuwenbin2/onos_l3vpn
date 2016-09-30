package org.onosproject.nel3vpnweb.codec;

import static com.google.common.base.Preconditions.checkNotNull;

import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.ne.VpnAc;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class VpnAcCodec extends JsonCodec<VpnAc> {
    public static final String OBJECTNODE_NOT_NULL = "ObjectNode can not be null";
    public static final String CODECCONTEXT_NOT_NULL = "CodecContext can not be null";

    @Override
    public ObjectNode encode(VpnAc vpnAc, CodecContext context) {
        checkNotNull(vpnAc, "vpnAc cannot be null");
        ObjectNode result = context.mapper().createObjectNode()
                .put("netVpnId", vpnAc.netVpnId() == null ? null
                   : vpnAc.netVpnId().toString())
                .put("acId", vpnAc.acId().toString())
                .put("acName", vpnAc.acName())
                .put("ipaddress", vpnAc.ipAddress().toString())
                .put("subNetMask", vpnAc.subNetMask().toString());

        return result;
    }

}
