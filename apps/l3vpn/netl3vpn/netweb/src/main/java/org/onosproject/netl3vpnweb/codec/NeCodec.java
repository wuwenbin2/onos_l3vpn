package org.onosproject.netl3vpnweb.codec;

import static com.google.common.base.Preconditions.checkNotNull;

import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.instance.nes.Ne;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class NeCodec extends JsonCodec<Ne> {
    public static final String OBJECTNODE_NOT_NULL = "ObjectNode can not be null";
    public static final String CODECCONTEXT_NOT_NULL = "CodecContext can not be null";

    @Override
    public ObjectNode encode(Ne ne, CodecContext context) {
        checkNotNull(ne, "vrfEntity cannot be null");
        ObjectNode result = context.mapper().createObjectNode()
                .put("neId", ne.id());
        return result;
    }

}
