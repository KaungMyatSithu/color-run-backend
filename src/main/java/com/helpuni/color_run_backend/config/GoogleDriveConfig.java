package com.helpuni.color_run_backend.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Configuration
public class GoogleDriveConfig {

    @Value("${google.drive.service-account-path}")
    private String serviceAccountPath;

    @Bean
    public Drive googleDriveService() throws Exception{

        InputStream serviceAccountStream = new ClassPathResource(serviceAccountPath).getInputStream();

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(serviceAccountStream)
                .createScoped(Collections.singleton(DriveScopes.DRIVE_FILE));

        HttpTransport transport;
        try{
            transport = GoogleNetHttpTransport.newTrustedTransport();
        }catch (GeneralSecurityException | java.io.IOException e){
            throw new IllegalStateException("Failed to create HTTP transport for Google Drive", e);
        }

        return new Drive.Builder(
                transport,
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("color-run-backend")
                .build();
    }
}
