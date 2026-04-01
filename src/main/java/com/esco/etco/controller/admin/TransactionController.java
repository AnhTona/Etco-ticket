package com.esco.etco.controller.admin;

import com.esco.etco.entity.request.ReqTransactionDTO;
import com.esco.etco.entity.response.ResTransactionDTO;
import com.esco.etco.service.TransactionService;
import com.esco.etco.util.annotation.ApiMessage;
import com.esco.etco.util.constant.ApiPaths;
import com.esco.etco.util.error.IdInvalidException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.TRANSACTIONS_API)
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    @ApiMessage("Tạo mới giao dịch")
    public ResponseEntity<ResTransactionDTO> createTransaction(@Valid @RequestBody ReqTransactionDTO reqTransactionDTO) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(reqTransactionDTO));
    }

    @PutMapping("/{id}")
    @ApiMessage("Cập nhật giao dịch")
    public ResponseEntity<ResTransactionDTO> updateTransaction(@PathVariable long id, @Valid @RequestBody ReqTransactionDTO reqTransactionDTO) throws IdInvalidException {
        return ResponseEntity.ok(transactionService.update(id, reqTransactionDTO));
    }

    @GetMapping("/{id}")
    @ApiMessage("Lấy giao dịch theo ID")
    public ResponseEntity<ResTransactionDTO> getTransactionById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(transactionService.getById(id));
        } catch (IdInvalidException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @ApiMessage("Lấy tất cả giao dịch")
    public ResponseEntity<List<ResTransactionDTO>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAll());
    }
}
