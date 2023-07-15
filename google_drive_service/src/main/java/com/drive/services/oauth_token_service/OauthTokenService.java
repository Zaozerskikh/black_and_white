package com.drive.services.oauth_token_service;

public interface OauthTokenService {
    String fetchToken(String code, String scope);
}
