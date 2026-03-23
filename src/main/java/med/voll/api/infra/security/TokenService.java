package med.voll.api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import med.voll.api.domain.usuarios.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.secret}")
    private String apiSecret;
    @Value("${api.security.token.expiracion-horas}")
    private int expiracionHoras;
    @Value("${api.security.token.zona-horaria}")
    private String zonaHoraria;

    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    public String generarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret);
            return JWT.create()
                    .withIssuer("voll med")
                    .withSubject(usuario.getLogin())
                    .withClaim("id", usuario.getId())
                    .withExpiresAt(generarFechaExpiracion())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            log.error("Error al generar el token JWT: {}", exception.getMessage());
            throw new RuntimeException("Error al generar el token JWT", exception);
        }
    }

    public String getSubject(String token) {
        if (token == null) {
            throw new RuntimeException("El token no puede ser nulo.");
        }

        try {
            Algorithm algorithm = Algorithm.HMAC256(apiSecret); // validando firma
            return JWT.require(algorithm)
                    .withIssuer("voll med")
                    .build()
                    .verify(token)// Si falla aquí, va directo al catch
                    .getSubject();// Si tiene éxito, retorna el subject inmediatamente

        } catch (JWTVerificationException exception) {
            log.error("Error en la verificacion del token: {}", exception.getMessage());
            throw new RuntimeException("Token inválido o expirado");
        }

    }

    private Instant generarFechaExpiracion() {
        return LocalDateTime.now().plusHours(expiracionHoras).toInstant(ZoneOffset.of(zonaHoraria));
    }

}
