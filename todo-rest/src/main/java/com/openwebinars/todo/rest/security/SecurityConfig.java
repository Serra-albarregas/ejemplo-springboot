package com.openwebinars.todo.rest.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración principal de seguridad de la aplicación usando Spring Security.
 *
 * Esta clase define:
 * - Cómo se autentican los usuarios (HTTP Basic)
 * - Qué rutas son públicas y cuáles requieren autenticación
 * - Cómo se manejan los errores de seguridad (401 y 403)
 * - Configuraciones adicionales como CORS, CSRF y headers
 */
@Configuration
@EnableWebSecurity // Activa la seguridad web en la aplicación
@EnableMethodSecurity // Permite usar @PreAuthorize y @PostAuthorize en los controladores
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Define la cadena de filtros de seguridad (SecurityFilterChain),
     * que controla todo el comportamiento de seguridad HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            /**
             * Habilita CORS con la configuración por defecto.
             * Permite que aplicaciones frontend (por ejemplo en otro puerto)
             * puedan consumir esta API.
             */
            .cors(Customizer.withDefaults())

            /**
             * Activa autenticación HTTP Basic.
             * El usuario deberá enviar username y password en cada petición.
             */
            .httpBasic(Customizer.withDefaults())

            /**
             * Define las reglas de autorización para las peticiones HTTP
             */
            .authorizeHttpRequests((authz) -> authz

                /**
                 * Permite acceso público a la documentación Swagger
                 */
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                /**
                 * Permite registrar nuevos usuarios sin autenticación
                 */
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()

                /**
                 * Permite peticiones OPTIONS (necesarias para CORS)
                 */
                .requestMatchers(HttpMethod.OPTIONS).permitAll()

                /**
                 * Cualquier otra petición requiere autenticación
                 */
                .anyRequest().authenticated()
            );

        /**
         * Desactiva CSRF (Cross-Site Request Forgery).
         * Es habitual en APIs REST que no usan sesiones.
         */
        http.csrf(csrf -> {
            csrf.disable();
        });

        /**
         * Desactiva la protección de frames (iframes).
         * Necesario, por ejemplo, para usar la consola H2 embebida.
         */
        http.headers(headers ->
            headers.frameOptions(opts -> opts.disable())
        );

        /**
         * Construye y devuelve la configuración de seguridad
         */
        return http.build();
    }


    /*
     * Configuración manual de CORS (comentada).
     * Se puede usar si necesitas controlar explícitamente:
     * - Orígenes permitidos
     * - Métodos HTTP permitidos
     *
     * Ejemplo: permitir solo localhost:9000 (frontend)
     */
    /*
    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origen permitido (frontend)
        configuration.setAllowedOrigins(List.of("http://localhost:9000"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","HEAD"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Aplica la configuración a todas las rutas
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    */
}