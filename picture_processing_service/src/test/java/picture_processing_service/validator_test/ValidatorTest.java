package picture_processing_service.validator_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.DefaultAopProxyFactory;
import org.springframework.mock.web.MockMultipartFile;
import picture_processing_service.controllers.PictureController;
import picture_processing_service.validators.InputImageFileValidator;

import java.lang.reflect.UndeclaredThrowableException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValidatorTest {
    private PictureController controllerProxy;

    @BeforeEach
    private void setUp() {
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(new PictureController());
        aspectJProxyFactory.addAspect(new InputImageFileValidator());
        controllerProxy = (PictureController) new DefaultAopProxyFactory().createAopProxy(aspectJProxyFactory).getProxy();
    }

    @Test
    @SuppressWarnings("all")
    void nullFileTest() {
        // exception in proxy class -> validator exception was thrown
        assertAll(
                () -> assertThrows(UndeclaredThrowableException.class, () -> controllerProxy.process(null, false, false, false)),
                () -> assertThrows(UndeclaredThrowableException.class, () -> controllerProxy.process(
                        new MockMultipartFile("inputFile", "test", null, new byte[0]), false, false, false))
        );
    }

    @Test
    void incorrectFormatsTest() {
        // exception in proxy class -> validator exception was thrown
        assertAll(
                () -> assertThrows(UndeclaredThrowableException.class, () -> controllerProxy.process(
                        new MockMultipartFile("inputFile", "test", "not_jpg", new byte[0]), false, false, false)),
                () -> assertThrows(UndeclaredThrowableException.class, () -> controllerProxy.process(
                        new MockMultipartFile("inputFile", "test", "psd", new byte[0]), false, false, false)),
                () -> assertThrows(UndeclaredThrowableException.class, () -> controllerProxy.process(
                        new MockMultipartFile("inputFile", "test", "tiff", new byte[0]), false, false, false))
        );
    }

    @Test
    void correctFormatsTest() {
        // exception in service -> validator exception was not thrown
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> controllerProxy.process(
                        new MockMultipartFile("inputFile", "test", "image/jpg", new byte[0]), false, false, false)),
                () -> assertThrows(NullPointerException.class, () -> controllerProxy.process(
                        new MockMultipartFile("inputFile", "test", "image/jpeg", new byte[0]), false, false, false)),
                () -> assertThrows(NullPointerException.class, () -> controllerProxy.process(
                        new MockMultipartFile("inputFile", "test", "image/jpe", new byte[0]), false, false, false))
        );
    }
}
