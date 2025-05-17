package raf.rs.messagingservice.service.implementation;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import raf.rs.messagingservice.service.FileService;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class RemoteFileUploadService implements FileService {

    private static final String baseUrl = "http://img.stamenic.work:8080/files";
    private final WebClient webClient;
    private final MeterRegistry meterRegistry;

    @Autowired
    public RemoteFileUploadService(WebClient.Builder webClientBuilder, MeterRegistry meterRegistry) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.meterRegistry = meterRegistry;
    }

    @Override
    @Timed(value = "messagingservice.remote.upload.time", description = "Time taken to upload a file to remote file system")
    public String uploadFile(MultipartFile file, Long textChannel) {
        log.info("Entering uploadFile with file: {}, textChannel: {}", file.getOriginalFilename(), textChannel);

        DistributionSummary summary = DistributionSummary
                .builder("messagingservice.remote.upload.size.bytes")
                .baseUnit("bytes")
                .description("Size of files uploaded remotely")
                .register(meterRegistry);
        summary.record(file.getSize());

        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", file.getResource());
        bodyMap.add("folder", textChannel.toString());

        try {
            Mono<String> response = webClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/upload").build())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyMap))
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorReturn("Error occurred while uploading file");

            String result = baseUrl + "/" + response.block();
            log.info("Exiting uploadFile with result: {}", result);
            return result;
        } catch (Exception e) {
            log.error("Error occurred while uploading file: {}", e.getMessage(), e);
            throw new RuntimeException("File upload failed", e);
        }
    }
}