package com.PoolingAPI.Service;

import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendSimpleMessage(String to, String subject, String text);
}
