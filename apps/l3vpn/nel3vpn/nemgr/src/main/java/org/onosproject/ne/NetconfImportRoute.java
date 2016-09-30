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
package org.onosproject.ne;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Represent the object for the xml element of importRoute.
 */
public class NetconfImportRoute {
    private final String operation;
    private final String importProtocol;
    private final String importProcessId;

    /**
     * NetconfImportRoute constructor.
     *
     * @param operation operation
     * @param importProtocol import protocol
     * @param importProcessId import process identifier
     */
    public NetconfImportRoute(String operation, String importProtocol,
                              String importProcessId) {
        checkNotNull(operation, "operation cannot be null");
        checkNotNull(importProtocol, "importProtocol cannot be null");
        checkNotNull(importProcessId, "importProcessId cannot be null");
        this.operation = operation;
        this.importProtocol = importProtocol;
        this.importProcessId = importProcessId;
    }

    /**
     * Returns operation.
     *
     * @return operation
     */
    public String operation() {
        return operation;
    }

    /**
     * Returns importProtocol.
     *
     * @return importProtocol
     */
    public String importProtocol() {
        return importProtocol;
    }

    /**
     * Returns importProcessId.
     *
     * @return importProcessId
     */
    public String importProcessId() {
        return importProcessId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, importProtocol, importProcessId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NetconfImportRoute) {
            final NetconfImportRoute other = (NetconfImportRoute) obj;
            return Objects.equals(this.operation, other.operation)
                    && Objects.equals(this.importProtocol, other.importProtocol)
                    && Objects.equals(this.importProcessId,
                                      other.importProcessId);
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringHelper(this).add("operation", operation)
                .add("importProtocol", importProtocol)
                .add("importProcessId", importProcessId).toString();
    }

    public ObjectNode objectNode() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode objNode = mapper.createObjectNode();
        objNode.put("operation", operation);
        objNode.put("importProtocol", importProtocol);
        objNode.put("importProcessId", importProcessId);
        return objNode;
    }
}
