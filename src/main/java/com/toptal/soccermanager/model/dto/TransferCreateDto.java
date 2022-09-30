package com.toptal.soccermanager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferCreateDto {
    private long playerId;

    @Min(0)
    private double askPrice;
}
