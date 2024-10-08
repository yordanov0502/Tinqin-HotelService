package com.tinqinacademy.hotel.api.operations.system.registervisitor;


import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.content.VisitorInput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RegisterVisitorInput implements OperationInput {
   @NotNull(message = "Visitors list cannot be null.")
   private List<@Valid VisitorInput> visitorInputList;
}
