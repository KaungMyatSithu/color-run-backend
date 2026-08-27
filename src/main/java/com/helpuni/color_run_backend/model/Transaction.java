package com.helpuni.color_run_backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    // The bank/payment transaction reference. It is also the Firestore document ID.
    private String transactionRefId;

    // Relationship to a registration document; no participant details are duplicated here.
    private String participantId;
    private int amountPaid;
    private boolean tallyCheck;
}
