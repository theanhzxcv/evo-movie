package com.evo.identity.infrastructure.adapter.tfa;

public interface TfaService {

    String generateSecretKey();

    boolean verifyCode(String secretKey, int verificationCode);
}
