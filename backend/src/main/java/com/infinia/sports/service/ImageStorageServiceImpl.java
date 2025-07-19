package com.infinia.sports.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    private static final Logger logger = LoggerFactory.getLogger(ImageStorageServiceImpl.class);

    @Value("${storage.location.images}")
    private String location;

    @Value("${storage.upload-path.images}")
    private String uploadPath;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            rootLocation = Paths.get(location);
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            logger.error("Could not initialize storage location", e);
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String storeImage(String imageUrl, String productName) throws IOException {
        if (imageUrl == null || imageUrl.isBlank()) {
            logger.warn("Image URL is null or empty for product: {}. Skipping download.", productName);
            return null;
        }

        try {
            String fileExtension = getFileExtension(imageUrl);
            String sanitizedFilename = sanitizeFilename(productName) + fileExtension;

            Path destinationFile = this.rootLocation.resolve(Paths.get(sanitizedFilename)).normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new IOException("Cannot store file outside current directory.");
            }

            logger.debug("Ruta de destino absoluta del fichero: {}", destinationFile);
            logger.info("Descargando imagen de {} a {}", imageUrl, destinationFile);

            try (InputStream in = new URL(imageUrl).openStream()) {
                Files.copy(in, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            String publicPath = uploadPath.endsWith("/") ? uploadPath + sanitizedFilename : uploadPath + "/" + sanitizedFilename;
            logger.debug("Ruta pública generada para la imagen: {}", publicPath);
            logger.info("Imagen almacenada con éxito. Ruta pública: {}", publicPath);
            return sanitizedFilename;

        } catch (IOException e) {
            logger.error("Failed to store image from URL: {}", imageUrl, e);
            throw e;
        }
    }

    private String sanitizeFilename(String filename) {
        String normalized = Normalizer.normalize(filename.toLowerCase(), Normalizer.Form.NFD);
        return normalized.replaceAll("\\s+", "-")
                         .replaceAll("[^\\p{ASCII}]", "")
                         .replaceAll("[^a-z0-9-\\.]", "")
                         .replaceAll("\\.{2,}", ".")
                         .replaceAll("[^a-z0-9-]", "");
    }

    private String getFileExtension(String url) {
        if (url == null || !url.contains(".")) {
            return ".jpg"; // Default extension if none found
        }
        return url.substring(url.lastIndexOf('.'));
    }
}
