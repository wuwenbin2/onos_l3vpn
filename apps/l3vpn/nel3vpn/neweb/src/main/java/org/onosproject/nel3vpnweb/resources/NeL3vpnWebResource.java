/*
 * Copyright 2015-present Open Networking Laboratory
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
package org.onosproject.nel3vpnweb.resources;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.OK;

import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.onlab.util.ItemNotFoundException;
import org.onosproject.ne.NeData;
import org.onosproject.ne.manager.NeL3vpnService;
import org.onosproject.nel3vpnweb.codec.NeDataCodec;
import org.onosproject.net.DeviceId;
import org.onosproject.rest.AbstractWebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * REST resource for l3vpn network element.
 */
@Path("ne")
public class NeL3vpnWebResource extends AbstractWebResource {
    public static final String NE_NOT_FOUND = "Network element is not found";
    public static final String CREATE_FAIL = "Create l3vpn failed";
    protected static final Logger log = LoggerFactory
            .getLogger(NeL3vpnWebResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listNedatas() {
        Iterable<NeData> nedatas = get(NeL3vpnService.class).getNeDatas();
        ObjectNode result = new ObjectMapper().createObjectNode();
        result.set("nedatas", new NeDataCodec().encode(nedatas, this));
        return ok(result.toString()).build();
    }

    @GET
    @Path("{neId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNedata(@PathParam("neId") String id) {
        NeData nedata = get(NeL3vpnService.class)
                .getNeData(DeviceId.deviceId(id));
        ObjectNode result = new ObjectMapper().createObjectNode();
        result.set("nedata", new NeDataCodec().encode(nedata, this));
        return ok(result.toString()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createL3vpn(InputStream input) {
        try {
            JsonNode cfg = this.mapper().readTree(input);
            NeData nedata = codec(NeData.class).decode((ObjectNode) cfg, this);
            Boolean issuccess = nullIsNotFound(get(NeL3vpnService.class)
                    .createL3vpn(nedata), NE_NOT_FOUND);
            if (!issuccess) {
                return Response.status(INTERNAL_SERVER_ERROR)
                        .entity(CREATE_FAIL).build();
            }
            return Response.status(OK).entity(issuccess.toString()).build();
        } catch (Exception e) {
            log.error("Creates l3vpn failed because of exception {}",
                      e.toString());
            return Response.status(INTERNAL_SERVER_ERROR).entity(e.toString())
                    .build();
        }
    }

    /**
     * Returns the specified item if that items is null; otherwise throws not
     * found exception.
     *
     * @param item item to check
     * @param <T> item type
     * @param message not found message
     * @return item if not null
     * @throws org.onlab.util.ItemNotFoundException if item is null
     */
    protected <T> T nullIsNotFound(T item, String message) {
        if (item == null) {
            throw new ItemNotFoundException(message);
        }
        return item;
    }

}
