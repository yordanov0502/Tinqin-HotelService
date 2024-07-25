package com.tinqinacademy.hotel.api.operations.system.getvisitors;


import lombok.*;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetVisitorsOutput {
    private List<VisitorOutput> visitorInputList;
}
