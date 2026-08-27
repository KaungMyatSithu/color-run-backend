package com.helpuni.color_run_backend.services;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.helpuni.color_run_backend.dto.TransactionDto;
import com.helpuni.color_run_backend.dto.TransactionListItemDto;
import com.helpuni.color_run_backend.model.Participant;
import com.helpuni.color_run_backend.model.Transaction;
import com.helpuni.color_run_backend.utils.DuplicateTransactionException;
import com.helpuni.color_run_backend.utils.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private static final String COLLECTION = "transactions";

    private final Firestore firestore;
    private final ParticipantService participantService;

    /**
     * Creates a transaction or saves a later change to the same reference ID.
     * A participant may have only one transaction tally record.
     */
    public Transaction save(TransactionDto request) {
        Participant participant = participantService.getById(request.getParticipantId());
        if (participant == null) {
            throw new ResourceNotFoundException("No registration found for ID: " + request.getParticipantId());
        }

        try {
            DocumentSnapshot referenceSnapshot = firestore.collection(COLLECTION)
                    .document(request.getTransactionRefId())
                    .get()
                    .get();

            if (referenceSnapshot.exists()) {
                Transaction existing = referenceSnapshot.toObject(Transaction.class);
                if (existing != null && !request.getParticipantId().equals(existing.getParticipantId())) {
                    throw new DuplicateTransactionException(
                            "Transaction reference ID is already assigned to another participant.");
                }
            } else {
                List<QueryDocumentSnapshot> participantTransactions = firestore.collection(COLLECTION)
                        .whereEqualTo("participantId", request.getParticipantId())
                        .limit(1)
                        .get()
                        .get()
                        .getDocuments();
                if (!participantTransactions.isEmpty()) {
                    throw new DuplicateTransactionException(
                            "A transaction already exists for participant ID: " + request.getParticipantId());
                }
            }

            Transaction transaction = Transaction.builder()
                    .transactionRefId(request.getTransactionRefId())
                    .participantId(request.getParticipantId())
                    .amountPaid(request.getAmountPaid())
                    .tallyCheck(request.getTallyCheck())
                    .build();

            firestore.collection(COLLECTION)
                    .document(transaction.getTransactionRefId())
                    .set(transaction)
                    .get();
            return transaction;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to save transaction", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to save transaction", e);
        }
    }

    /**
     * Returns one row per registered participant, including registrations not yet tallied.
     */
    public List<TransactionListItemDto> transactionList() {
        try {
            List<QueryDocumentSnapshot> transactionDocuments = firestore.collection(COLLECTION)
                    .get()
                    .get()
                    .getDocuments();
            Map<String, Transaction> transactionsByParticipant = new HashMap<>();
            for (QueryDocumentSnapshot document : transactionDocuments) {
                Transaction transaction = document.toObject(Transaction.class);
                if (transaction != null) {
                    transactionsByParticipant.put(transaction.getParticipantId(), transaction);
                }
            }

            List<Participant> participants = participantService.registrationList(null);
            List<TransactionListItemDto> result = new ArrayList<>();
            for (Participant participant : participants) {
                Transaction transaction = transactionsByParticipant.get(participant.getParticipantId());
                result.add(new TransactionListItemDto(
                        participant.getParticipantId(),
                        participant.getStudentName(),
                        participant.getPriceType(),
                        participant.getPaymentReceiptUrl() != null && !participant.getPaymentReceiptUrl().isBlank(),
                        participant.getReceiptId(),
                        transaction == null ? null : transaction.getTransactionRefId(),
                        transaction == null ? null : transaction.getAmountPaid(),
                        transaction != null && transaction.isTallyCheck()
                ));
            }
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to list transactions", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Failed to list transactions", e);
        }
    }
}
