package org.onosproject.nel3vpnweb.codec;

import static com.google.common.base.Preconditions.checkNotNull;

import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.ne.VrfEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class VrfEntityCodec extends JsonCodec<VrfEntity> {
    public static final String OBJECTNODE_NOT_NULL = "ObjectNode can not be null";
    public static final String CODECCONTEXT_NOT_NULL = "CodecContext can not be null";

    @Override
    public ObjectNode encode(VrfEntity vrfEntity, CodecContext context) {
        checkNotNull(vrfEntity, "vrfEntity cannot be null");
        ObjectNode result = context.mapper().createObjectNode()
                .put("netVpnId", vrfEntity.netVpnId() == null ? null
                    : vrfEntity.netVpnId().toString())
                .put("routeDistinguisher", vrfEntity.routeDistinguisher().toString())
                .put("importTargets", vrfEntity.importTargets().toString())
                .put("exportTargets", vrfEntity.exportTargets().toString())
                .put("acIdList", vrfEntity.acIdList().toString());
        result.set("bgp", new BgpCodec().encode(vrfEntity.bgp(), context));
        return result;
    }
}
