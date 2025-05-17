package raf.rs.messagingservice.service.implementation;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import raf.rs.messagingservice.exception.FolderNotFoundException;
import raf.rs.messagingservice.service.FileService;
import raf.rs.messagingservice.service.TextChannelService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final Drive driveService;

    @Autowired
    private TextChannelService textChannelService;

    public FileServiceImpl() throws IOException {
        this.driveService = null;
        // should implement this
    }

    @Override
    public String uploadFile(MultipartFile file, Long textChannel) {
        log.info("Entering uploadFile with file: {}, textChannel: {}", file.getOriginalFilename(), textChannel);

        String folderId = textChannelService.getFolderIdFromTextChannel(textChannel);

        if (folderId == null) {
            log.error("Folder for text channel {} not found", textChannel);
            throw new FolderNotFoundException("Folder for text channel " + textChannel + " not found");
        }

        try {
            String fileId = createFileInDrive(file, folderId);
            log.info("Exiting uploadFile with result: {}", fileId);
            return fileId;
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String createFileInDrive(MultipartFile file, String folderId) throws IOException {
        log.info("Entering createFileInDrive with file: {}, folderId: {}", file.getOriginalFilename(), folderId);

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
        createRequest.getMediaHttpUploader().setProgressListener(progress -> log.info("Upload progress: {}", progress.getProgress()));

        com.google.api.services.drive.model.File uploadedFile = createRequest.execute();

        log.info("Exiting createFileInDrive with result: {}", uploadedFile.getId());
        return uploadedFile.getId();
    }

    public void listFilesInFolder(String folderId) throws IOException {
        log.info("Entering listFilesInFolder with folderId: {}", folderId);

        String query = "'" + folderId + "' in parents";

        Drive.Files.List request = driveService.files().list();
        request.setQ(query);
        request.setFields("nextPageToken, files(id, name)");
        request.setPageSize(100);

        FileList fileList = request.execute();

        List<com.google.api.services.drive.model.File> files = fileList.getFiles();
        if (files == null || files.isEmpty()) {
            log.info("No files found in folder {}", folderId);
        } else {
            log.info("Files in folder {}:", folderId);
            for (File file : files) {
                log.info("File name: {}, File ID: {}", file.getName(), file.getId());
            }
        }

        log.info("Exiting listFilesInFolder");
    }

    public void deleteFile(String fileId) {
        log.info("Entering deleteFile with fileId: {}", fileId);

        try {
            driveService.files().delete(fileId).execute();
            log.info("Exiting deleteFile");
        } catch (IOException e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}