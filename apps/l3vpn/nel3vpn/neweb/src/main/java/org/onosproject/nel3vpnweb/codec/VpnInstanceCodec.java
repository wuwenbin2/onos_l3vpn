package org.onosproject.nel3vpnweb.codec;

import static com.google.common.base.Preconditions.checkNotNull;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.ne.VpnInstance;

import com.fasterxml.jackson.databind.node.ObjectNode;

public final class VpnInstanceCodec extends JsonCodec<VpnInstance> {
    public static final String OBJECTNODE_NOT_NULL = "ObjectNode can not be null";
    public static final String CODECCONTEXT_NOT_NULL = "CodecContext can not be null";

    @Override
    public ObjectNode encode(VpnInstance vpnInstance, CodecContext context) {
        checkNotNull(vpnInstance, "vpnInstancea cannot be null");
        ObjectNode result = context.mapper().createObjectNode()
                .put("neId", vpnInstance.neId().toString());
        result.set("vrfList",
                   new VrfEntityCodec().encode(vpnInstance.vrfList(), context));
        return result;
    }

}
