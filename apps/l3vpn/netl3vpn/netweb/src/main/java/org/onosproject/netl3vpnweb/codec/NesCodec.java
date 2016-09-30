package org.onosproject.netl3vpnweb.codec;

import static com.google.common.base.Preconditions.checkNotNull;

import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.yang.gen.v1.net.l3vpn.rev20160701.netl3vpn.instances.instance.Nes;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class NesCodec extends JsonCodec<Nes> {
    public static final String OBJECTNODE_NOT_NULL = "ObjectNode can not be null";
    public static final String CODECCONTEXT_NOT_NULL = "CodecContext can not be null";

    @Override
    public ObjectNode encode(Nes nes, CodecContext context) {
        checkNotNull(nes, "nes cannot be null");
        ObjectNode result = context.mapper().createObjectNode();
        result.set("nes", new NeCodec().encode(nes.ne(), context));
        return result;
    }

}
