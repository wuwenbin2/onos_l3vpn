package org.onosproject.ne.manager;

import java.util.Collection;
import org.onosproject.ne.NeData;
import org.onosproject.net.DeviceId;

public interface NeL3vpnService {

    /**
     * Creates L3vpn.
     *
     * @param nedata the data of l3vpn ne.
     * @return boolean
     */
    boolean createL3vpn(NeData nedata);

    /**
     * Return all NeData.
     *
     * @return collection of NeData.
     */
    Collection<NeData> getNeDatas();

    /**
     * Return NeData with the specified identifier .
     *
     * @return collection of NeData.
     */
    NeData getNeData(DeviceId deviceId);

    /**
     * Returns true if deviceid exits in nedata store.
     *
     * @param deviceId device identifier
     * @return true or false
     */
    boolean exists(DeviceId deviceId);
}
