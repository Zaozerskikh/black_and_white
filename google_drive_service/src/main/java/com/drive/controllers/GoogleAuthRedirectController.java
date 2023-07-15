package com.drive.controllers;

import com.drive.enums.SessionKey;
import com.drive.services.oauth_token_service.OauthTokenService;
import com.drive.services.oauth_token_service.OauthTokenServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.stream.Stream;

@Controller
public class GoogleAuthRedirectController {
    @Autowired
    private OauthTokenService oauthTokenService;

    @RequestMapping("/oauth2/callback/google")
    public String callbackUrl(HttpServletRequest request, HttpSession httpSession) {
        String code = request.getParameter("code");
        String[] scopes = request.getParameter("scope").split(" ");

        String scopeWithPermissions =
                Stream.of(scopes)
                        .filter(s -> s.contains("drive"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("no permission"));
        httpSession.setAttribute(
                SessionKey.GOOGLE_OAUTH_TOKEN.toString(),
                oauthTokenService.fetchToken(code, scopeWithPermissions)
        );
        return "redirect:/files";
    }
}
