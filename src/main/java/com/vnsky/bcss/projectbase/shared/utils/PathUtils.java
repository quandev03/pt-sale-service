package com.vnsky.bcss.projectbase.shared.utils;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class PathUtils {

    public boolean isStartPath(String fileUrl, String folder) {
        Path filePath = Paths.get(fileUrl).normalize();
        Path folderPath = Paths.get(folder).normalize();

        return filePath.startsWith(folderPath);
    }

    public boolean checkStartPath(String fileUrl, String folder) {
        if (fileUrl != null && folder != null) {
            List<String> lstUrl = Arrays.stream(fileUrl.split("/")).toList();
            return lstUrl.contains(folder.split("/")[0]);
        }
        return false;
    }

    public String getExtension(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public String getExtension(Path path) {
        String fileName = path.normalize().toString();
        return getExtension(fileName);
    }

}
