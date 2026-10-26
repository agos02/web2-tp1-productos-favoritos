package ar.edu.unvime.apiblank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ar.edu.unvime.apiblank.security.JwtAuthenticationFilter;
import ar.edu.unvime.apiblank.security.JwtService;
import ar.edu.unvime.apiblank.security.ManejadorErroresSeguridad;

/**
 * Configuración de seguridad de la API: qué rutas son públicas, cuáles exigen un token JWT válido
 * y cómo se responden los errores 401 (sin token o token inválido) y 403 (sin permiso).
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   ManejadorErroresSeguridad manejadorErrores) throws Exception {
        http
                // Sin cookies ni sesiones: la identidad viaja en el token de cada request,
                // por eso CSRF (que protege sesiones con cookies) no aplica.
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/productos/**",
                                "/health",
                                "/error",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint(manejadorErrores)
                        .accessDeniedHandler(manejadorErrores))
                .addFilterBefore(new JwtAuthenticationFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** Hashea contraseñas con BCrypt (cada hash incluye su propia sal aleatoria). */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}