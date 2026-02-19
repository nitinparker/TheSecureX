package com.thesecurex.portal.service;

import com.thesecurex.portal.model.Evidence;
import com.thesecurex.portal.model.User;
import com.thesecurex.portal.repository.EvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.UUID;

@Service
public class EvidenceVaultService {

    private final Path rootLocation;

    @Autowired
    private EvidenceRepository evidenceRepository;

    public EvidenceVaultService(@Value("${evidence.vault.path:evidence-vault}") String vaultPath) {
        this.rootLocation = Paths.get(vaultPath);
        try {
            Files.createDirectories(this.rootLocation);
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    public Evidence store(MultipartFile file, User user, String type) throws Exception {
        // 1. Create unique filename to prevent collisions
        String originalFileName = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path destinationFile = this.rootLocation.resolve(storedFileName);

        // 2. Calculate SHA-256 Hash and Save File simultaneously
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(destinationFile)) {
            
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
                os.write(buffer, 0, read);
            }
        }

        String hash = bytesToHex(digest.digest());

        // 3. Save Metadata to Database
        Evidence evidence = new Evidence();
        evidence.setFileName(originalFileName);
        evidence.setFileType(type);
        evidence.setFileSize(file.getSize());
        evidence.setSha256Hash(hash);
        evidence.setStoragePath(destinationFile.toString());
        evidence.setUploadedBy(user);

        return evidenceRepository.save(evidence);
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public boolean verifyIntegrity(Long evidenceId) throws Exception {
        Evidence evidence = evidenceRepository.findById(evidenceId)
                .orElseThrow(() -> new RuntimeException("Evidence not found"));
        
        Path file = Paths.get(evidence.getStoragePath());
        if (!Files.exists(file)) {
            throw new RuntimeException("File missing from vault: " + evidence.getFileName());
        }

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream is = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                digest.update(buffer, 0, read);
            }
        }

        String currentHash = bytesToHex(digest.digest());
        return currentHash.equals(evidence.getSha256Hash());
    }
}
