package com.example.dslist.services;

import com.example.dslist.dto.EmailDTO;
import com.example.dslist.dto.NewPasswordDTO;
import com.example.dslist.entities.PasswordRecover;
import com.example.dslist.entities.User;
import com.example.dslist.repositories.PasswordRecoverRepository;
import com.example.dslist.repositories.UserRepository;
import com.example.dslist.services.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createRecoverToken(EmailDTO body) {
        User user = userRepository.findByEmail(body.email());
        if (user == null) {
            throw new ResourceNotFoundException("Email não encontrado");
        }

        String token = UUID.randomUUID().toString();

        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(body.email());
        entity.setToken(token);
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));

        entity = passwordRecoverRepository.save(entity);

        String text = "Acesse o link para definir uma nova senha\n\n"
                + recoverUri + token
                + "\n\n Token: " + token
                + "\n\n Validade de " + tokenMinutes + " minutos";

        emailService.sendEmail(body.email(), "Recuperação de Senha", text);
    }

    @Transactional
    public void saveNewPassword(@Valid NewPasswordDTO body) {

        List<PasswordRecover> result = passwordRecoverRepository.searchValidTokens(body.token(), Instant.now());
        if (result.isEmpty()){
            throw new ResourceNotFoundException("Token inválido");
        }

        User user = userRepository.findByEmail(result.get(0).getEmail());
        user.setPassword(passwordEncoder.encode(body.password()));
        user = userRepository.save(user);

    }

    protected User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return userRepository.findByEmail(username);
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Invalid user");
        }
    }

}
