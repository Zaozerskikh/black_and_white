package picture_processing_service.validators;

import lombok.SneakyThrows;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import picture_processing_service.utils.PictureException;

import java.util.Set;

/**
 * Validator, that checks input file in http request.
 */
@Aspect
@Configuration
public class InputImageFileValidator {
    private final Set<String> supportedFormats = Set.of("jpg", "jpeg", "jpe");

    @SneakyThrows
    @Before("execution(* picture_processing_service.controllers.PictureController.process(..))")
    @SuppressWarnings("all")
    public void validateInputFile(JoinPoint jp) {
        Object file = jp.getArgs()[0];
        if (file == null) {
            throw new PictureException("input image was null");
        }

        String type = ((MultipartFile)file).getContentType();
        if (type == null) {
            throw new PictureException("input image was null");
        }

        if (!type.contains("/") || type.charAt(type.length() - 1) == '/' ||
                !supportedFormats.contains(type.substring(type.indexOf("/") + 1))) {
            throw new PictureException("incorrect image ext");
        }
    }
}
