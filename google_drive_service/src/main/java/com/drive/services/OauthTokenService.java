package com.drive.services;

import com.drive.dto.OauthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OauthTokenService {
    @Autowired
    private WebClient webClient;

    public String fetchToken(String code, String scope) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", "http://localhost:8080/oauth2/callback/google");
        formData.add("scope", scope);

        return webClient.post()
                .uri("https://accounts.google.com/o/oauth2/token")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(OauthResponse.class)
                .block()
                .getAccess_token();

    }
}
