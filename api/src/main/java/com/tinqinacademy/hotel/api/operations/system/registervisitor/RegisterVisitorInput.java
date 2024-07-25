package com.tinqinacademy.hotel.api.operations.system.registervisitor;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RegisterVisitorInput {
   @NotNull
   private List<VisitorInput> visitorInputList;
}
