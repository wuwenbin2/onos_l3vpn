package org.onosproject.nel3vpnweb.codec;

import static com.google.common.base.Preconditions.checkNotNull;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.ne.BgpImportProtocol;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class BgpImportProtocolCodec extends JsonCodec<BgpImportProtocol> {
    public static final String OBJECTNODE_NOT_NULL = "ObjectNode can not be null";
    public static final String CODECCONTEXT_NOT_NULL = "CodecContext can not be null";

    @Override
    public ObjectNode encode(BgpImportProtocol bgpImportProtocol,
                             CodecContext context) {
        checkNotNull(bgpImportProtocol, "bgpImportProtocol cannot be null");
        ObjectNode result = context.mapper().createObjectNode()
                .put("protocolType",
                     bgpImportProtocol.protocolType().toString())
                .put("processId", bgpImportProtocol.processId().toString());
        return result;
    }
}
