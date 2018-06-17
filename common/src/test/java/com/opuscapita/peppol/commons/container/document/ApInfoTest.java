package com.opuscapita.peppol.commons.container.document;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by bambr on 17.12.7.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ApInfoTest {
    private final String onlyApId = "AP_666";
    private final String commonNameWithDetails = "CN=APP_1000000208,O=OpusCapita,C=FI";
    private final String commonNameWithDetailsInOldFormat = "O=EVRY Norge AS,CN=APP_1000000148,C=NO";

    @Test
    public void parseFromCommonNameOnlyApId() {
        ApInfo apInfo = ApInfo.parseFromCommonName(onlyApId);
        assertNotNull(apInfo);
        assertEquals("AP_666", apInfo.getId());
        assertEquals("AP_666", apInfo.getName());
        assertNull(apInfo.getCountry());
    }

    @Test
    public void parseFromCommonNameWithDetails() {
        ApInfo apInfo = ApInfo.parseFromCommonName(commonNameWithDetails);
        assertNotNull(apInfo);
        assertEquals("APP_1000000208", apInfo.getId());
        assertEquals("OpusCapita", apInfo.getName());
        assertEquals("FI", apInfo.getCountry());

        apInfo = ApInfo.parseFromCommonName(commonNameWithDetailsInOldFormat);
        assertNotNull(apInfo);
        assertEquals("APP_1000000148", apInfo.getId());
        assertEquals("EVRY Norge AS", apInfo.getName());
        assertEquals("NO", apInfo.getCountry());
    }


}