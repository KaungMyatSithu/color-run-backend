package com.helpuni.color_run_backend.services;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.helpuni.color_run_backend.model.Admin;
import com.helpuni.color_run_backend.utils.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AdminService {
    private static final String COLLECTION = "admins";
    private final Firestore firestore;

    public FirebaseToken verifyToken(String idToken)throws FirebaseAuthException {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    public Admin getByUid(String uid){
        try {
            DocumentReference docRef = firestore.collection(COLLECTION).document(uid);
            DocumentSnapshot doc = docRef.get().get();
            if (!doc.exists()){
                throw new ResourceNotFoundException("No admin profile found for this account");
            }
            return doc.toObject(Admin.class);

        }catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to fetch admin profile", e);
        }
    }

    public Admin createAdminProfile(Admin request){
        Admin admin = Admin.builder()
                .uid(request.getUid())
                .name(request.getName())
                .phoneNo(request.getPhoneNo())
                .build();
        try {
            firestore.collection(COLLECTION).document(admin.getUid()).set(admin).get();
            return admin;
        }catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to create admin profile", e);
        }
    }
}
