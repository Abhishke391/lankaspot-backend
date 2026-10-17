package com.lankaspot.server.dto.openRetrievals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllDistricts {
    List<DistrictDTO> districts;
}
