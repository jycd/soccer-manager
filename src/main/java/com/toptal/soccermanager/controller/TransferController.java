package com.toptal.soccermanager.controller;

import com.toptal.soccermanager.configuration.exception.ApplicationException;
import com.toptal.soccermanager.model.dto.PagingDataReqDto;
import com.toptal.soccermanager.model.dto.PagingDataRespDto;
import com.toptal.soccermanager.model.dto.TransferDto;
import com.toptal.soccermanager.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * User can read all transfers
 */

@RestController
@Validated
@RequestMapping(value = "/transfers")
public class TransferController {
    @Autowired
    public TransferService transferService;

    @GetMapping("/{id}")
    public ResponseEntity<TransferDto> getById(@PathVariable("id") long id) throws ApplicationException {
        TransferDto existTransfer = transferService.getById(id);

        return ResponseEntity.ok(existTransfer);
    }

    @GetMapping
    public ResponseEntity<?> getAll(@Pattern(regexp = "[\\s]*[0-9]*[1-9]+[0-9]*[\\s]*", message = "size must be greater than 0") @RequestParam(required = false, name = "size") String size,
                                    @Pattern(regexp = "[\\s]*[0-9]+[\\s]*", message = "page must be greater than or equal to 0") @RequestParam(required = false, name = "page") String page) throws ApplicationException {
        if (size == null && page == null) {
            List<TransferDto> existTransfers = transferService.getAll();
            return ResponseEntity.ok(existTransfers);
        } else {
            int pageSize;
            int pageNumber;
            if (size == null) {
                pageSize = 50;
            } else {
                pageSize = Integer.parseInt(size.trim());
            }
            if (page == null) {
                pageNumber = 0;
            } else {
                pageNumber = Integer.parseInt(page.trim());
            }
            if (pageSize > 100) {
                pageSize = 100;
            }
            PagingDataRespDto<TransferDto> existTransfers = transferService.getAll(new PagingDataReqDto(pageNumber, pageSize));
            return ResponseEntity.ok(existTransfers);
        }
    }
}
