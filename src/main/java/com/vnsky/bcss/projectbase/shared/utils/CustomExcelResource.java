package com.vnsky.bcss.projectbase.shared.utils;

import lombok.EqualsAndHashCode;
import org.springframework.core.io.ByteArrayResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Custom Resource cho Excel files
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class CustomExcelResource extends ByteArrayResource {

    @EqualsAndHashCode.Include
    private final String filename;

    // Dùng hash nội dung để hỗ trợ so sánh/equals qua annotation
    @EqualsAndHashCode.Include
    private final int contentHash;

    public CustomExcelResource(byte[] byteArray, String filename) {
        super(byteArray);
        this.filename = filename;
        this.contentHash = Arrays.hashCode(byteArray);
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public long contentLength() {
        return getByteArray().length;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(getByteArray());
    }

}
