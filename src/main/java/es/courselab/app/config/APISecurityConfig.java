package es.courselab.app.config;


import es.courselab.app.jwt.AuthEntryPointJwt;
import es.courselab.app.jwt.AuthTokenFilter;
import es.courselab.app.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.server.WebFilter;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class APISecurityConfig {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    /**
     * PasswordEncoder para encriptar y validar contraseñas (BCrypt)
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ReactiveAuthenticationManager basado en tu ReactiveUserDetailsService (UserServiceImpl)
     * Esto es equivalente al DaoAuthenticationProvider + AuthenticationManager de tu configuración Servlet.
     * @param passwordEncoder
     * @return
     */
    @Bean
    public ReactiveAuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userService);
        manager.setPasswordEncoder(passwordEncoder);
        return manager;
    }

    /**
     * 3) Cadena de seguridad REACTIVA para rutas protegidas (equivale a filterChain(HttpSecurity) en Servlet).
     * <p>
     * - Desactiva CSRF (stateless API).
     * - Usa tu AuthEntryPointJwt reactivo para devolver 401 con JSON.
     * - Indica que NO se use ninguna sesión (stateless).
     * - Inyecta el ReactiveAuthenticationManager que definimos más arriba.
     * - Permite TODAS las peticiones a /auth/**, /swagger-ui/**, /v3/api-docs/** y /dev-check/**.
     * - Exige autenticación (JWT) en cualquier otra ruta.
     * - Añade AuthTokenFilter justo en el orden de AUTHENTICATION.
     */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public SecurityWebFilterChain apiSecurityFilterChain(ServerHttpSecurity http,
                                                         ReactiveAuthenticationManager authManager,
                                                         WebFilter authTokenFilter) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(unauthorizedHandler)
                )
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authenticationManager(authManager)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/**", "/notify/**", "/swagger-ui/**", "/v3/api-docs/**", "/dev-check/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(authTokenFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
