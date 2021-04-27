package com.serguni.messenger.controllers;

import com.google.gson.Gson;
import com.serguni.messenger.components.security.GenerationKeyService;
import com.serguni.messenger.components.security.SecretKeyGenerator;
import com.serguni.messenger.dbms.models.Configuration;
import com.serguni.messenger.dbms.models.User;
import com.serguni.messenger.dbms.repositories.TemporaryKeyRepository;
import com.serguni.messenger.dbms.repositories.UserRepository;
import com.serguni.messenger.utils.CryptoUtil;
import com.serguni.messenger.utils.RegExValidUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final TemporaryKeyRepository temporaryKeyRepository;

    public UserController(UserRepository userRepository,
                          TemporaryKeyRepository temporaryKeyRepository) {
        this.userRepository = userRepository;
        this.temporaryKeyRepository = temporaryKeyRepository;
    }

    @PostMapping("/users")
    public ResponseEntity<?> signUp(@RequestBody User user) {
        String warning = "";

        if (userRepository.existsByEmail(user.getEmail())) {
            warning += "User with this email already exists! \n";
        }
        if (userRepository.existsByNickname(user.getNickname())) {
            warning += "User with this nickname already exists!";
        }

        if (warning.length() == 0) {

            user.setConfiguration(new Configuration(user));
            User savedUser = userRepository.save(user);
            System.out.println(savedUser);

            GenerationKeyService generationKeyService = new GenerationKeyService(temporaryKeyRepository);
            generationKeyService.createKey(savedUser);

            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(warning, HttpStatus.OK);
        }
    }

    @GetMapping("/users/{login}")
    public ResponseEntity<?> logIn(@PathVariable String login) {
        System.out.println(login);
        if (!RegExValidUtil.checkEmail(login) && !RegExValidUtil.checkStandard(login)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = userRepository.findByEmail(login);
        System.out.println(user);

        if (user == null) {
            user = userRepository.findByNickname(login);
        }

        if (user != null) {
            GenerationKeyService generationKeyService = new GenerationKeyService(temporaryKeyRepository);
            generationKeyService.createKey(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



}
