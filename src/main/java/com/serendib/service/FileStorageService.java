package com.serendib.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");
    private static final Set<String> RECEIPT_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "pdf");
    private final Path uploadRoot;

    public FileStorageService(@Value("${serendib.upload.dir:data/uploads}") String uploadDir) {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        try { Files.createDirectories(uploadRoot); } catch (IOException e) { throw new RuntimeException(e); }
    }

    public String saveImage(MultipartFile file) { return save(file, "images", IMAGE_EXTENSIONS, 12 * 1024 * 1024L); }
    public String saveReceipt(MultipartFile file) {
        String url = save(file, "receipts", RECEIPT_EXTENSIONS, 20 * 1024 * 1024L);
        return url.substring(url.lastIndexOf('/') + 1);
    }

    public Path getReceiptPath(String filename) {
        if (filename == null || !filename.matches("[a-fA-F0-9]{32}\\.(jpg|jpeg|png|webp|pdf)")) {
            throw new IllegalArgumentException("Invalid receipt file.");
        }
        Path path = uploadRoot.resolve("receipts").resolve(filename).normalize();
        if (!path.startsWith(uploadRoot.resolve("receipts").normalize())) throw new IllegalArgumentException("Invalid receipt path.");
        return path;
    }

    private String save(MultipartFile file, String folder, Set<String> allowed, long maxBytes) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Please select a file.");
        if (file.getSize() > maxBytes) throw new IllegalArgumentException("File is too large.");
        String original = file.getOriginalFilename() == null ? "upload" : file.getOriginalFilename();
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0 && dot < original.length() - 1) ext = original.substring(dot + 1).toLowerCase(Locale.ROOT);
        if (!allowed.contains(ext)) throw new IllegalArgumentException("Unsupported file type.");

        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path dir = uploadRoot.resolve(folder).normalize();
        Path target = dir.resolve(filename).normalize();
        if (!target.startsWith(uploadRoot)) throw new IllegalArgumentException("Invalid upload path.");
        try {
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not save uploaded file.", e);
        }
        return "/uploads/" + folder + "/" + filename;
    }

    public String getUploadRootUri() { return uploadRoot.toUri().toString(); }
}
