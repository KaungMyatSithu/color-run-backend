package com.helpuni.color_run_backend.dto;

import com.helpuni.color_run_backend.model.enums.Gender;
import com.helpuni.color_run_backend.model.enums.TshirtSize;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantRegistration {

    @AssertTrue(message = "You must agreed to the Terms & Conditions.")
    private boolean tncAgreement;
    @NotBlank(message = "Student name required.")
    @Size(max = 150)
    private String studentName;
    @Min(value = 18, message = "Participant must be at least 18")
    @Max(value = 100)
    private int age;
    @NotNull(message = "Gender is required")
    private Gender gender;
    @NotBlank
    @Pattern(regexp = "\\d{6}-\\d{2}-\\d{4}", message = "IC number must be in format XXXXXX-XX-XXXX")
    private String icNumber;
    @NotBlank
    @Pattern(regexp = "^60\\d{8,9}$", message = "Phone number must start with 60 and contain only digits")
    private String phoneNumber;
    @NotBlank
    @Email(message = "Must be a valid email")
    @Pattern(regexp = "^(?!.*@helplive\\.email$).*$", message = "Institutional email is not allowed - use a personal email")
    private String personalEmail;
    @NotBlank
    @Size(max = 100)
    private String emergencyContactName;
    @NotBlank
    @Pattern(regexp = "^60\\d{8,9}$", message = "Emergency contact number must start with 60")
    private String emergencyContactNumber;
    @NotBlank
    @Size(max = 100)
    private String emergencyRelation;
    @NotBlank
    @Size(max = 150)
    private String universityName;
    @NotBlank
    @Size(max = 100)
    private String studyProgramme;
    @NotNull(message = "T-shirt size is required")
    private TshirtSize tshirtSize;


}
