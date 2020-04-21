package com.netcracker.educ.printing.model.representationModel;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ResponseRepresent {
    @NotNull
    private UUID executorId;

    @NotNull
    private UUID orderId;

    private float sum;
}