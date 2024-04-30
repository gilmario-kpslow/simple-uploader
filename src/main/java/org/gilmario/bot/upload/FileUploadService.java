package org.gilmario.bot.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import javax.inject.Singleton;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 *
 * @author gilmario
 */
@Singleton
public class FileUploadService {

    String UPLOAD_DIR;

    public String uploadFile(MultipartFormDataInput input) {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("file");
        for (InputPart inputPart : inputParts) {
            try {
                MultivaluedMap<String, String> header
                        = inputPart.getHeaders();
                InputStream inputStream = inputPart.getBody(InputStream.class, null);
                writeFile(inputStream, getFileName(header));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "Files Successfully Uploaded";
    }

    private void writeFile(InputStream inputStream, String fileName)
            throws IOException {
        byte[] bytes = IOUtils.toByteArray(inputStream);
        File customDir = new File(UPLOAD_DIR);
        fileName = customDir.getAbsolutePath()
                + File.separator + fileName;
        Files.write(Paths.get(fileName), bytes,
                StandardOpenOption.CREATE_NEW);
    }

    private String getFileName(MultivaluedMap<String, String> header) {
        String[] contentDisposition = header.
                getFirst("Content-Disposition").split(";");
        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {
                String[] name = filename.split("=");
                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "";
    }
}
