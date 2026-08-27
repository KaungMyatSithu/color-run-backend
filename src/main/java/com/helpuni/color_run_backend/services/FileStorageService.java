package com.helpuni.color_run_backend.services;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.helpuni.color_run_backend.utils.InvalidFileException;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final List<String> IMAGE_TYPES = List.of("image/jpeg", "image/jpg", "image/png");
    private static final List<String> RECEIPT_TYPE = List.of("image/jpeg", "image/jpg", "image/png", "application/pdf");

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final Drive driveService;

    @Value("${google.drive.folder-id}")
    private String folderId;

    public String uploadStudentCard(MultipartFile file, String participantId){
        try {
            validateContentType(file,IMAGE_TYPES,"Student card must be JPG, JPEG, or PNG");
            return upload(file,"student-card",participantId);
        } catch (InvalidFileException e) {
            throw new InvalidFileException("Fail to Upload Student Card.");
        }
    }

    public String uploadPaymentReceipt(MultipartFile file, String participantId){
        try {
            validateContentType(file,RECEIPT_TYPE,"Receipt must be JPG, PNG, or PDF");
            return upload(file,"receipt",participantId);
        }catch (InvalidFileException e){
            throw new InvalidFileException("Fail to Upload Payment Receipt.");
        }
    }

    private void validateContentType(MultipartFile file, List<String> allowed, String message) throws InvalidFileException {
        if(file == null || file.isEmpty()) {
            throw new InvalidFileException("File is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !allowed.contains(contentType.toLowerCase())) {
            throw new InvalidFileException(message);
        }

    }

    private String upload(MultipartFile file, String label, String participantId){
        try {

            String fileName = participantId + "_" + label + "_" + UUID.randomUUID() + "_"
                    + file.getOriginalFilename();
            File Metadata = new File();
            Metadata.setName(fileName);
            Metadata.setParents(Collections.singletonList(folderId));

            ByteArrayContent content = new ByteArrayContent(file.getContentType(),file.getBytes());

            File uploaded = driveService.files()
                    .create(Metadata, content)
                    .setFields("id, webViewLink, webContentLink")
                    .execute();

            // File Readable via Link permission
            Permission permission = new Permission();
            permission.setType("anyone");
            permission.setRole("reader");
            driveService.permissions().create(uploaded.getId(), permission).execute();

            // webContentLink returns user downloadable link
            return uploaded.getWebContentLink();

        }catch(IOException e){
            logger.error("Drive upload failed for participant {}: {}", participantId, e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to Google Drive", e);
        }
    }
}
