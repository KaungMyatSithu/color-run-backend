package com.helpuni.color_run_backend.dto;

import com.helpuni.color_run_backend.model.enums.PriceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionListItemDto {
    private final String participantId;
    private final String participantName;
    private final PriceType priceType;
    private final boolean receiptUploaded;
    private final String receiptId;
    private final String transactionRefId;
    private final Integer amountPaid;
    private final boolean tallyCheck;
}
