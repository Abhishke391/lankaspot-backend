package com.lankaspot.server;

import com.lankaspot.server.enums.AdHistoryAction;
import com.lankaspot.server.model.AdQuote;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AdvertisementDomainModelTests {

    @Test
    void quoteStoresAContentSnapshotWithoutAEntityReference() {
        UUID ref = UUID.randomUUID();
        AdQuote quote = new AdQuote(ref, 1, "House", "house",
                "Description", BigDecimal.TEN, "Colombo", "Colombo",
                "Area", "Address", "image-1");

        assertEquals(ref, quote.getAdvertisementRef());
        assertEquals(1, quote.getVersion());
        assertEquals("House", quote.getTitle());
        assertNotNull(quote.getCreatedAt());
    }

    @Test
    void amendmentAndRenewalAreExplicitHistoryActions() {
        assertEquals(AdHistoryAction.AMENDMENT, AdHistoryAction.valueOf("AMENDMENT"));
        assertEquals(AdHistoryAction.RENEWAL, AdHistoryAction.valueOf("RENEWAL"));
    }
}
