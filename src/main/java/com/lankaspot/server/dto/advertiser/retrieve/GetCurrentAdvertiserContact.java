package com.lankaspot.server.dto.advertiser.retrieve;

import java.util.List;

public record GetCurrentAdvertiserContact(
        List<ContactDTO> phoneNumbers
) {
}
