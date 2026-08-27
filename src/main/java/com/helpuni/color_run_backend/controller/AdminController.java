package com.helpuni.color_run_backend.controller;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.helpuni.color_run_backend.dto.AdminProfileRequest;
import com.helpuni.color_run_backend.dto.UpdateStatusRequest;
import com.helpuni.color_run_backend.model.Admin;
import com.helpuni.color_run_backend.model.Participant;
import com.helpuni.color_run_backend.model.enums.RegistrationStatus;
import com.helpuni.color_run_backend.services.AdminService;
import com.helpuni.color_run_backend.services.ParticipantService;
import com.helpuni.color_run_backend.utils.HttpResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/admins")
@RequiredArgsConstructor
public class AdminController {

    private final ParticipantService participantService;
    private final AdminService adminService;

    @PostMapping("/profile")
    public ResponseEntity<HttpResponse<Admin>> createProfile(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody AdminProfileRequest request) throws FirebaseAuthException {

        String idToken = authHeader.replace("Bearer ", "");
        FirebaseToken decodedToken = adminService.verifyToken(idToken);

        Admin admin = Admin.builder()
                .uid(decodedToken.getUid())
                .name(request.getName())
                .phoneNo(request.getPhoneNo())
                .build();

        Admin created = adminService.createAdminProfile(admin);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(HttpResponse.of(201, "Admin profile created", created));
    }

    @GetMapping("/profile")
    public ResponseEntity<HttpResponse<Admin>> getProfile(
            @RequestHeader("Authorization") String authHeader) throws FirebaseAuthException {

        String idToken = authHeader.replace("Bearer ", "");
        FirebaseToken decodedToken = adminService.verifyToken(idToken);
        Admin admin = adminService.getByUid(decodedToken.getUid());
        return ResponseEntity.ok(HttpResponse.of(200, "Found", admin));
    }

// Participant Management Part
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
