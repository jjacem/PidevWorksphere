package esprit.tn.utils;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class CloudinaryUploader {

    private static final String CLOUD_NAME = "dfinw6lmg";
    private static final String API_KEY = "273431972826225";
    private static final String API_SECRET = "KhAfNJB5T9TuSYkojn8lpqcx4hk";

    public static String uploadPdfToCloudinary(byte[] pdfData) {
        try {
            // Save the PDF data to a temporary file
            File tempFile = File.createTempFile("equipe", ".pdf");
            Files.write(tempFile.toPath(), pdfData);

            // Configure Cloudinary
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", CLOUD_NAME,
                    "api_key", API_KEY,
                    "api_secret", API_SECRET
            ));

            // Upload the PDF file to Cloudinary and make it publicly accessible
            Map<?, ?> uploadResult = cloudinary.uploader().upload(tempFile, ObjectUtils.asMap(
                    "resource_type", "raw", // Set resource type to "raw" for non-image files
                    "public_id", "equipe_pdf", // Optional: Set a custom public ID
                    "access_mode", "public" // Make the file publicly accessible
            ));

            // Get the shareable URL
            String pdfUrl = (String) uploadResult.get("secure_url");

            // Delete the temporary file
            tempFile.delete();

            return pdfUrl;

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to upload PDF to Cloudinary: " + e.getMessage());
            return null;
        }
    }
}