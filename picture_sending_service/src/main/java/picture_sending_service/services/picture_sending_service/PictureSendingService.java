package picture_sending_service.services.picture_sending_service;

import reactor.core.publisher.Mono;

public interface PictureSendingService {
    Mono<byte[]> sendSingle();
}
