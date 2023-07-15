package com.drive.services.oauth_token_service;

import com.drive.dto.OauthResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class OauthTokenServiceImpl implements OauthTokenService {
    @Autowired
    private WebClient googleDriveTokenRequestSenderWebClient;

    @Value("${server.base_url}")
    private String baseUrl;

    @Override
    public String fetchToken(String code, String scope) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", baseUrl + "/oauth2/callback/google");
        formData.add("scope", scope);

        return googleDriveTokenRequestSenderWebClient.post()
                .uri("https://accounts.google.com/o/oauth2/token")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(OauthResponse.class)
                .block()
                .getAccess_token();

    }
}
