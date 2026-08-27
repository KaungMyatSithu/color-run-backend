package com.helpuni.color_run_backend.model;

import com.google.cloud.firestore.annotation.ServerTimestamp;
import com.helpuni.color_run_backend.model.enums.Gender;
import com.helpuni.color_run_backend.model.enums.RegistrationStatus;
import com.helpuni.color_run_backend.model.enums.TshirtSize;
import com.helpuni.color_run_backend.model.enums.PriceType;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participant {
    private String participantId; // we use the IC number as the document ID
    private boolean tncAgreement;
    private String studentName;
    private int age;
    private Gender gender;
    private String icNumber;
    private String phoneNumber;
    private String personalEmail;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String emergencyRelation;
    private String universityName;
    private String studyProgramme;
    private String studentCardUrl;
    private TshirtSize tshirtSize;
    private PriceType priceType;
    private String receiptId;
    private String paymentReceiptUrl;
    @ServerTimestamp
    private Date registrationDate;
    private RegistrationStatus registrationStatus;
}
