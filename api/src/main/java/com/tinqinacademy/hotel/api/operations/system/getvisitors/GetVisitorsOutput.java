package com.tinqinacademy.hotel.api.operations.system.getvisitors;


import com.tinqinacademy.hotel.api.base.OperationOutput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.content.VisitorOutput;
import lombok.*;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetVisitorsOutput implements OperationOutput {
    private List<VisitorOutput> visitorOutputList;
}
