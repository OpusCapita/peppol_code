package com.opuscapita.peppol.events.persistence.controller;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by bambr on 17.12.7.
 */
public class ApInfoTest {
    String onlyApId = "AP_666";
    String commonNameWithDetails = "CN=APP_1000000208,O=OpusCapita,C=FI";
    String commonNameWithDetailsInOldFormat = "O=EVRY Norge AS,CN=APP_1000000148,C=NO";

    @Test
    public void parseFromCommonNameOnlyApId() throws Exception {
        ApInfo apInfo = ApInfo.parseFromCommonName(onlyApId);
        assertNotNull(apInfo);
        assertNotNull(apInfo.getId());
        assertNull(apInfo.getName());
        assertNull(apInfo.getCountry());
    }

    @Test
    public void parseFromCommonNameWithDetails() throws Exception {
        ApInfo apInfo = ApInfo.parseFromCommonName(commonNameWithDetails);
        assertNotNull(apInfo);
        assertNotNull(apInfo.getId());
        assertNotNull(apInfo.getName());
        assertNotNull(apInfo.getCountry());
        apInfo = null;

        apInfo = ApInfo.parseFromCommonName(commonNameWithDetailsInOldFormat);
        assertNotNull(apInfo);
        assertNotNull(apInfo.getId());
        assertNotNull(apInfo.getName());
        assertNotNull(apInfo.getCountry());
    }


}