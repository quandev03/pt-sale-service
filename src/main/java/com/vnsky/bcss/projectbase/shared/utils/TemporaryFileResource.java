package com.vnsky.bcss.projectbase.shared.utils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
@EqualsAndHashCode(callSuper = true)
public class TemporaryFileResource extends FileSystemResource {

    private static final Path BASE_PATH = Paths.get("./tmp").toAbsolutePath().normalize();

    static {
        if (!Files.exists(BASE_PATH)) {
            try {
                Files.createDirectories(BASE_PATH);
            } catch (IOException e) {
                throw new UnsupportedOperationException(e.getMessage());
            }
        }
    }

    private String originalFileName;

    public TemporaryFileResource(String suffix) throws IOException {
        super(Files.createTempFile(BASE_PATH, null, suffix));
    }

    public TemporaryFileResource() throws IOException {
        this(null);
    }

    public static TemporaryFileResource from(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String ext = getExtension(originalFilename);
        TemporaryFileResource temporaryFileResource = new TemporaryFileResource(ext);
        multipartFile.transferTo(temporaryFileResource.getFile());
        temporaryFileResource.originalFileName = originalFilename;
        return temporaryFileResource;
    }

    @Nullable
    private static String getExtension(String originalFilename) {
        String ext = null;
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex >= 0) {
                ext = originalFilename.substring(dotIndex);
            }
        }
        return ext;
    }

    @Override
    @NonNull
    public InputStream getInputStream() throws IOException {
        return getInputStream(true);
    }

    public InputStream getInputStream(boolean delete) throws IOException {
        if (delete) {
            return new FileInputStream(getFile()) {
                @Override
                public void close() throws IOException {
                    super.close();
                    cleanup();
                }
            };
        }
        return super.getInputStream();
    }

    public void cleanup() throws IOException {
        Path path = Paths.get(getPath());
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    public String getExtension() {
        return getExtension(this.getFilename());
    }

}
