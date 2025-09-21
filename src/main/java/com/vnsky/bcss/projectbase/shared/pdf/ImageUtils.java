package com.vnsky.bcss.projectbase.shared.pdf;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ImageUtils {

    private ImageUtils() {
    }

    public static String resizeImageAndConvertToBase64(InputStream imageData, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(imageData);
        Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bufferedResizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedResizedImage.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedResizedImage, "jpg", baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public static BufferedImage combineImages(BufferedImage[] images) {
        if (images == null || images.length == 0) {
            throw new IllegalArgumentException("No images to combine");
        }

        // Calculate the width and height of the combined image
        int combinedWidth = 0;
        int combinedHeight = 0;
        for (BufferedImage image : images) {
            combinedWidth = Math.max(combinedWidth, image.getWidth());
            combinedHeight += image.getHeight();
        }

        // Create a new image with the combined dimensions
        BufferedImage combinedImage = new BufferedImage(combinedWidth, combinedHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combinedImage.createGraphics();

        // Draw each image onto the combined image
        int currentHeight = 0;
        for (BufferedImage image : images) {
            g2d.drawImage(image, 0, currentHeight, null);
            currentHeight += image.getHeight();
        }
        g2d.dispose();

        return combinedImage;
    }
}
