package ar.edu.unvime.apiblank.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Responde los errores de seguridad con el mismo formato JSON que el resto de la API:
 * 401 cuando falta el token (o es inválido/vencido) y 403 cuando el usuario no tiene permiso.
 */
@Component
public class ManejadorErroresSeguridad implements AuthenticationEntryPoint, AccessDeniedHandler {

    /** Se invoca cuando se accede a una ruta protegida sin una identidad válida: 401. */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        escribirError(request, response, HttpStatus.UNAUTHORIZED,
                "Se requiere un token válido: falta, es inválido o está vencido");
    }

    /** Se invoca cuando el usuario está autenticado pero no tiene permiso: 403. */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        escribirError(request, response, HttpStatus.FORBIDDEN,
                "No tenés permiso para acceder a este recurso");
    }

    private void escribirError(HttpServletRequest request,
                               HttpServletResponse response,
                               HttpStatus status,
                               String mensaje) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String json = """
                {"timestamp":"%s","status":%d,"error":"%s","message":"%s","path":"%s"}"""
                .formatted(
                        LocalDateTime.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        escapar(mensaje),
                        escapar(request.getRequestURI())
                );

        response.getWriter().write(json);
    }

    /** Escapa los caracteres que romperían el JSON armado a mano. */
    private String escapar(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}