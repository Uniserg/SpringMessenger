package com.serguni.messenger.components.security;


import com.serguni.messenger.components.mail.EmailService;
import com.serguni.messenger.components.mail.EmailServiceImpl;
import com.serguni.messenger.dbms.models.TemporaryKey;
import com.serguni.messenger.dbms.models.User;
import com.serguni.messenger.dbms.repositories.TemporaryKeyRepository;
import org.springframework.stereotype.Service;

@Service
public class GenerationKeyService {

    private final TemporaryKeyRepository temporaryKeyRepository;

    public GenerationKeyService(TemporaryKeyRepository temporaryKeyRepository) {
        this.temporaryKeyRepository = temporaryKeyRepository;
    }

    public TemporaryKey createKey(User user) {
        TemporaryKey key;
        TemporaryKeyGenerator generator = new TemporaryKeyGenerator();
        String newKey = generator.getTemporaryKey();
        key = new TemporaryKey(newKey, user);

        EmailService emailService = new EmailServiceImpl();
        emailService.sendSimpleMessage(user.getEmail(),
                "Authorization",
                "Your temporary code:" + key.getKey());

        System.out.println(key.getKey());
        temporaryKeyRepository.save(key);
        return key;
    }
}
