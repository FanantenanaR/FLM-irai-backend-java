package com.flm.irai.auth.service;

import com.flm.irai.auth.dto.LoginRequest;
import com.flm.irai.auth.dto.RegisterRequest;
import com.flm.irai.auth.dto.TokenResponse;
import com.flm.irai.utilisateur.model.Utilisateur;
import com.flm.irai.utilisateur.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("L'email est deja utilise");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .email(request.getEmail())
                .motDePasseHash(passwordEncoder.encode(request.getMdp()))
                .kristianaId(request.getKristianaId())
                .estActif(true)
                .build();

        utilisateurRepository.save(utilisateur);

        return generateTokens(utilisateur.getEmail());
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMdp())
        );

        if (!authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Authentification echouee");
        }

        return generateTokens(request.getEmail());
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        try {
            String email = jwtService.extractUsername(refreshToken);
            Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Refresh token invalide"));

            if (!jwtService.isTokenValid(refreshToken, new com.flm.irai.auth.model.UtilisateurDetails(utilisateur))) {
                throw new IllegalArgumentException("Refresh token invalide ou expire");
            }

            return generateTokens(email);
        } catch (Exception e) {
            throw new IllegalArgumentException("Refresh token invalide ou corrompu");
        }
    }

    private TokenResponse generateTokens(String email) {
        var userDetails = utilisateurRepository.findByEmail(email)
                .map(com.flm.irai.auth.model.UtilisateurDetails::new)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        String access = jwtService.generateAccessToken(userDetails);
        String refresh = jwtService.generateRefreshToken(userDetails);

        return TokenResponse.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .tokenType("Bearer")
                .build();
    }
}
