package ar.edu.unvime.apiblank.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ar.edu.unvime.apiblank.model.Rol;
import ar.edu.unvime.apiblank.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/** Emite y valida los tokens JWT con los que los usuarios se identifican en cada petición. */
@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMinutos;

    public JwtService(@Value("${app.jwt.secret}") String secreto,
                      @Value("${app.jwt.expiration-minutes}") long expiracionMinutos) {
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMinutos = expiracionMinutos;
    }

    /** Crea un token firmado que lleva adentro el id y el rol del usuario. */
    public String generarToken(Usuario usuario) {
        Date ahora = new Date();
        Date vencimiento = new Date(ahora.getTime() + expiracionMinutos * 60_000);

        return Jwts.builder()
                .subject(String.valueOf(usuario.getId()))
                .claim("rol", usuario.getRol().name())
                .issuedAt(ahora)
                .expiration(vencimiento)
                .signWith(clave)
                .compact();
    }

    /** Devuelve la identidad del token, o vacío si está mal formado, adulterado o vencido. */
    public Optional<UsuarioAutenticado> validarToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long id = Long.valueOf(claims.getSubject());
            Rol rol = Rol.valueOf(claims.get("rol", String.class));
            return Optional.of(new UsuarioAutenticado(id, rol));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}