package com.helpuni.color_run_backend.controller;

import com.helpuni.color_run_backend.dto.TransactionDto;
import com.helpuni.color_run_backend.dto.TransactionListItemDto;
import com.helpuni.color_run_backend.model.Transaction;
import com.helpuni.color_run_backend.services.TransactionService;
import com.helpuni.color_run_backend.utils.HttpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<HttpResponse<Transaction>> saveTransaction(
            @Valid @RequestBody TransactionDto request) {
        Transaction saved = transactionService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(HttpResponse.of(201, "Transaction saved.", saved));
    }

    @GetMapping
    public ResponseEntity<HttpResponse<List<TransactionListItemDto>>> getTransactions() {
        List<TransactionListItemDto> transactions = transactionService.transactionList();
        return ResponseEntity.ok(HttpResponse.of(200,
                "Found " + transactions.size() + " participant transaction records.", transactions));
    }
}
