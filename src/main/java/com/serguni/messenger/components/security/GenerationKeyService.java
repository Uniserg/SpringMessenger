package com.serguni.messenger.components.security;


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

    public void createKey(User user) {
        TemporaryKey key;
        KeyGenerator keyGenerator = new KeyGenerator();
        String newKey = keyGenerator.getTemporaryKey();
        key = new TemporaryKey(newKey, user);

        System.out.println(key.getKey());
        temporaryKeyRepository.save(key);
    }
}
