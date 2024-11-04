package com.bankApp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern; // Add this import
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileUpload {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "File path is required")
    private String filePath;

    @NotBlank(message = "File type is required")
    @Pattern(regexp = "^(image|document|video|audio)$", message = "File type must be either 'image', 'document', 'video', or 'audio'")
    private String fileType;

    @NotNull(message = "User is required")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
