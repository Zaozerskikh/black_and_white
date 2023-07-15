package com.drive.controllers;

import com.drive.enums.SessionKey;
import lombok.extern.java.Log;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpSession;

@Aspect
@Log
@Configuration
public class GoogleAuthRedirectAuthAspect {
    @After("execution(* com.drive.controllers.GoogleAuthRedirectController.callbackUrl(..))")
    public void clearSessionRequestParams(JoinPoint jp) {
        HttpSession session = (HttpSession) jp.getArgs()[1];

        session.removeAttribute(SessionKey.SOURCE_FOLDER_ID.toString());
        session.removeAttribute(SessionKey.OUTPUT_FOLDER_NAME.toString());
        session.removeAttribute(SessionKey.BW.toString());
        session.removeAttribute(SessionKey.VINGETTE.toString());
        session.removeAttribute(SessionKey.BLUR_BACKGROUND.toString());

        log.info("session was cleared");
    }
}
