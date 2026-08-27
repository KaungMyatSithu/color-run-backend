package com.helpuni.color_run_backend.controller;

import com.helpuni.color_run_backend.dto.ParticipantRegistration;
import com.helpuni.color_run_backend.model.Participant;
import com.helpuni.color_run_backend.services.ParticipantService;
import com.helpuni.color_run_backend.utils.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;
    private final ObjectMapper objMapper;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<HttpResponse<Participant>> participantRegistration(
            @RequestPart("data") String dataJason,
            @RequestPart("studentCard") MultipartFile studentCard,
            @RequestPart("paymentReceipt") MultipartFile paymentReceipt
            ) throws Exception{

        ParticipantRegistration request =
                objMapper.readValue(dataJason,ParticipantRegistration.class);

        // Manual Validate when @Valid doesn't apply to JSON String.
        var violations = jakarta.validation.Validation.buildDefaultValidatorFactory()
                .getValidator().validate(request);
        if(!violations.isEmpty()){
            StringBuilder sb = new StringBuilder();
            violations.forEach(v -> sb.append(v.getPropertyPath()).append(": ")
                    .append(v.getMessage()).append("; "));
            return ResponseEntity.badRequest().body(HttpResponse.of(400,sb.toString(),null));
        }

        Participant saved = participantService.register(request,studentCard,paymentReceipt);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(HttpResponse.of(201, "Registration Submitted, pending Validation.", saved));
    }

    @GetMapping("/{participantId}")
    public ResponseEntity<HttpResponse<Participant>> getParticipantById(@PathVariable String participantId){
        Participant participant = participantService.getById(participantId);
        return ResponseEntity.ok(HttpResponse.of(200,"Found",participant));
    }
}
