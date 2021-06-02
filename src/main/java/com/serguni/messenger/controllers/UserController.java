package com.serguni.messenger.controllers;

import com.serguni.messenger.components.security.GenerationKeyService;
import com.serguni.messenger.dbms.models.Configuration;
import com.serguni.messenger.dbms.models.User;
import com.serguni.messenger.dbms.repositories.TemporaryKeyRepository;
import com.serguni.messenger.dbms.repositories.UserRepository;
import com.serguni.messenger.utils.RegExValidUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
            user.setLastOnline(new Date(0));
            User savedUser = userRepository.save(user);
            System.out.println(savedUser);

            GenerationKeyService generationKeyService = new GenerationKeyService(temporaryKeyRepository);
            generationKeyService.createKey(savedUser);

            return new ResponseEntity<>(savedUser.getId(), HttpStatus.CREATED);
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
            return new ResponseEntity<>(user.getId(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



}
