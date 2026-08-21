package com.helpuni.color_run_backend.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.helpuni.color_run_backend.dto.ParticipantRegistration;
import com.helpuni.color_run_backend.model.Participant;
import com.helpuni.color_run_backend.model.enums.RegistrationStatus;
import com.helpuni.color_run_backend.utils.DuplicateRegistrationException;
import com.helpuni.color_run_backend.utils.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ParticipantService {
    private static final String COLLECTION = "registrations";
    private final Firestore firestore;
    private final FileStorageService fileService;

    public Participant register(ParticipantRegistration request,
                                MultipartFile studentCard,
                                MultipartFile paymentReceipt){

        DocumentReference docRef = firestore.collection(COLLECTION).document(request.getIcNumber());
        try {
            if(docRef.get().get().exists()){
                throw new DuplicateRegistrationException("This IC Number is already Registered.");
            }
            if (isEmailTaken(request.getPersonalEmail())){
                throw new DuplicateRegistrationException("This Email is already Registered.");
            }
        }catch (InterruptedException | ExecutionException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to check for duplicate registration", e);
        }

        //Upload studentCard and Receipt
        String studentCardUrl = fileService.uploadStudentCard(studentCard, request.getIcNumber());
        String paymentReceiptUrl = fileService.uploadPaymentReceipt(paymentReceipt,request.getIcNumber());

        Participant participant = Participant.builder()
                .participantId(request.getIcNumber())
                .tncAgreement(request.isTncAgreement())
                .studentName(request.getStudentName())
                .age(request.getAge())
                .gender(request.getGender())
                .icNumber(request.getIcNumber())
                .phoneNumber(request.getPhoneNumber())
                .personalEmail(request.getPersonalEmail())
                .emergencyContactName(request.getEmergencyContactName())
                .emergencyContactNumber(request.getEmergencyContactNumber())
                .emergencyRelation(request.getEmergencyRelation())
                .universityName(request.getUniversityName())
                .studyProgramme(request.getStudyProgramme())
                .studentCardUrl(studentCardUrl)
                .tshirtSize(request.getTshirtSize())
                .paymentReceiptUrl(paymentReceiptUrl)
                .registrationStatus(RegistrationStatus.Pending)
                .build();
        try {
            docRef.set(participant).get();
        }catch (InterruptedException | ExecutionException e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to save registration", e);
        }
        return participant;
    }

    //Admin Part
    public Participant getById(String participantId){
        try{
            DocumentSnapshot doc = firestore.collection(COLLECTION).document(participantId).get().get();
            if (!doc.exists()){
                throw new ResourceNotFoundException("No registration found for ID: "+ participantId);
            }
            return doc.toObject(Participant.class);
        }catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to fetch registration", e);
        }
    }

    public List<Participant> registrationList(RegistrationStatus status){
        try {
            Query query = firestore.collection(COLLECTION);
            if(status != null){
                query =query.whereEqualTo("registrationStatus", status.name());
            }

            List<QueryDocumentSnapshot> docs = query.get().get().getDocuments();
            List<Participant> result = new ArrayList<>();
            for (QueryDocumentSnapshot doc : docs){
                result.add(doc.toObject(Participant.class));
            }
            return result;
        }catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to list registrations", e);
        }
    }

    public Participant updateStatus(String participantId, RegistrationStatus newStatus){
        DocumentReference docRef = firestore.collection(COLLECTION).document(participantId);
        try {
            if(docRef.get().get().exists()){
                throw new ResourceNotFoundException("No registration found for ID: " + participantId);
            }
            docRef.update("registrationStatus", newStatus.name()).get();
            return getById(participantId);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to update registration status", e);
        }
    }

    private boolean isEmailTaken(String email) throws ExecutionException, InterruptedException{
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION)
                .whereEqualTo("personalEmail",email)
                .limit(1)
                .get();
        return !future.get().isEmpty();
    }
}
