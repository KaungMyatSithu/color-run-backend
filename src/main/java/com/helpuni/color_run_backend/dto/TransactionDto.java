package com.helpuni.color_run_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDto {

    @NotBlank(message = "Participant ID is required.")
    private String participantId;

    @NotBlank(message = "Transaction reference ID is required.")
    @Pattern(regexp = "^[^/\\s]{1,100}$", message = "Transaction reference ID cannot contain spaces or '/'.")
    private String transactionRefId;

    @PositiveOrZero(message = "Amount paid must be zero or greater.")
    private int amountPaid;

    @NotNull(message = "Tally check is required.")
    private Boolean tallyCheck;
}
