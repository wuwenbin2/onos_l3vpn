package org.onosproject.nel3vpnweb.codec;

import static com.google.common.base.Preconditions.checkNotNull;

import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.ne.Bgp;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class BgpCodec extends JsonCodec<Bgp> {
    public static final String OBJECTNODE_NOT_NULL = "ObjectNode can not be null";
    public static final String CODECCONTEXT_NOT_NULL = "CodecContext can not be null";

    @Override
    public ObjectNode encode(Bgp bgp, CodecContext context) {
        checkNotNull(bgp, "bgp cannot be null");
        ObjectNode result = context.mapper().createObjectNode();
        result.set("bgp", new BgpImportProtocolCodec()
                .encode(bgp.importProtocols(), context));
        return result;
    }
}
