package com.toptal.soccermanager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingDataRespDto<T> {
    private long totalElements;
    private int totalPage;
    private List<T> data;
}

