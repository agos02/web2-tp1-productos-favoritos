package ar.edu.unvime.apiblank.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Lee el header "Authorization: Bearer <token>", valida el token y deja la identidad del usuario
 * disponible para el resto del request. Si no hay token o es inválido, no registra ninguna identidad.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(PREFIJO_BEARER)) {
            String token = header.substring(PREFIJO_BEARER.length());

            jwtService.validarToken(token).ifPresent(usuario -> {
                UsernamePasswordAuthenticationToken autenticacion = new UsernamePasswordAuthenticationToken(
                        usuario,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + usuario.rol().name()))
                );
                SecurityContextHolder.getContext().setAuthentication(autenticacion);
            });
        }

        filterChain.doFilter(request, response);
    }
}