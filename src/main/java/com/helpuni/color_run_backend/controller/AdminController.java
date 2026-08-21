package com.helpuni.color_run_backend.controller;

import com.helpuni.color_run_backend.dto.UpdateStatusRequest;
import com.helpuni.color_run_backend.model.Participant;
import com.helpuni.color_run_backend.model.enums.RegistrationStatus;
import com.helpuni.color_run_backend.services.ParticipantService;
import com.helpuni.color_run_backend.utils.HttpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AdminController {

    private final ParticipantService participantService;

    @GetMapping
    public ResponseEntity<HttpResponse<List<Participant>>> showAllParticipants(
            @RequestParam(required = false)RegistrationStatus status){

        List<Participant> participants = participantService.registrationList(status);
        return ResponseEntity.ok(HttpResponse.of(200,
                "Found "+participants.size()+" registrations.", participants));
    }

    @PatchMapping("/{participantId}/status")
    public ResponseEntity<HttpResponse<Participant>> updateStatus(
            @PathVariable String participantId,
            @Valid @RequestBody UpdateStatusRequest request) {
        Participant updated = participantService.updateStatus(participantId, request.getRegStatus());
        return ResponseEntity.ok(HttpResponse.of(200, "Status updated to " + request.getRegStatus(), updated));
    }
}
