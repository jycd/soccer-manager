package com.toptal.soccermanager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingDataReqDto {
    @Min(0)
    private int pageNumber;

    @Min(1)
    @Max(100)
    private int pageSize;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PagingDataReqDto)) return false;
        PagingDataReqDto that = (PagingDataReqDto) o;
        return pageNumber == that.pageNumber && pageSize == that.pageSize;
    }
}
