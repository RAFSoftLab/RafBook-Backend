package raf.rs.messagingservice.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import raf.rs.messagingservice.service.FileService;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.BodyInserters;

@Service
public class RemoteFileUploadService implements FileService {

    private static final String baseUrl = "http://img.stamenic.work:8080/files";
    private final WebClient webClient;

    @Autowired
    public RemoteFileUploadService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public String uploadFile(MultipartFile file, Long textChannel) {

        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("file", file.getResource());
        bodyMap.add("folder", textChannel.toString());

        Mono<String> response = webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/upload")
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyMap))
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("Error occurred while uploading file");

        return baseUrl + "/" + response.block();
    }
}
