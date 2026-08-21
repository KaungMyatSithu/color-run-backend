package com.helpuni.color_run_backend.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    private String uid;       // Firebase Auth UID, also the document ID
    private String name;
    private String phoneNo;
}
