package com.lankaspot.server.dto.advertiser.savePropertyInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveAdvertPropertyInfoResponseData {

    /** The id of the saved AdQuote. Steps 2 to 4 send it back as quoteRef. */
    private Long quoteRef;
}