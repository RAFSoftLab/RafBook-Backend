package raf.rs.messagingservice.service.implementation;

import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import raf.rs.messagingservice.exception.FolderNotFoundException;
import raf.rs.messagingservice.service.FileService;
import raf.rs.messagingservice.service.TextChannelService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Service
public class FileServiceImpl implements FileService {


    private final Drive driveService;

    @Autowired
    private TextChannelService textChannelService;

    public FileServiceImpl() throws IOException {
        this.driveService = null;
        //should implement this
    }

    @Override
    public String uploadFile(MultipartFile file, Long textChannel) {

        String folderId;
        folderId = textChannelService.getFolderIdFromTextChannel(textChannel);

        if (folderId == null) {
            throw new FolderNotFoundException("Folder for text channel " + textChannel + " not found");
        }

        try {
            return createFileInDrive(file, folderId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String createFileInDrive(MultipartFile file, String folderId) throws IOException {

        listFilesInFolder(folderId);

        com.google.api.services.drive.model.File fileMetadata = new com.google.api.services.drive.model.File();
        fileMetadata.setName(file.getOriginalFilename())
                .setParents(Collections.singletonList(folderId));

        InputStreamContent mediaContent = new InputStreamContent(
                "application/octet-stream",
                file.getInputStream()
        );
        mediaContent.setLength(file.getSize());

        Drive.Files.Create createRequest = driveService.files().create(fileMetadata, mediaContent);

        createRequest.getMediaHttpUploader().setProgressListener(progress -> System.out.println("Upload progress: " + progress.getProgress()));

        com.google.api.services.drive.model.File uploadedFile = createRequest.execute();

        return uploadedFile.getId();
    }

    public void listFilesInFolder(String folderId) throws IOException {
        // Query to list files in a specific folder
        String query = "'" + folderId + "' in parents";

        Drive.Files.List request = driveService.files().list();
        request.setQ(query);  // Set query to fetch files in the specified folder
        request.setFields("nextPageToken, files(id, name)");  // Define the fields to return
        request.setPageSize(100);  // Limit the number of files to fetch per request

        FileList fileList = request.execute();

        List<com.google.api.services.drive.model.File> files = fileList.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files in folder " + folderId + ":");
            for (File file : files) {
                System.out.println("File name: " + file.getName() + ", File ID: " + file.getId());
            }
        }
    }

    public void deleteFile(String fileId) {

        try {
            driveService.files().delete(fileId).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
