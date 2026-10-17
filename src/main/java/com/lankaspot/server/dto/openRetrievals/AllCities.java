package com.lankaspot.server.dto.openRetrievals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllCities {
    List<CityDTO> cities;
}
