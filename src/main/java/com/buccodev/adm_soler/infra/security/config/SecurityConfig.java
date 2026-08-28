package com.buccodev.adm_soler.infra.security.config;

import com.buccodev.adm_soler.infra.security.handler.RestAccessDeniedHandler;
import com.buccodev.adm_soler.infra.security.handler.RestAuthenticationEntryPoint;
import com.buccodev.adm_soler.infra.security.jwt.JwtAuthenticationFilter;
import com.buccodev.adm_soler.infra.security.jwt.JwtProperties;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Seguranca da API: stateless, sem sessao e sem cookie.
 *
 * <h2>Perfis</h2>
 * <ul>
 *   <li><b>ADMIN</b> - gestao: usuarios, perfis e qualquer exclusao.</li>
 *   <li><b>USER</b> - operacao diaria: cria e edita cadastros, nao exclui e
 *       nao mexe em usuarios.</li>
 *   <li><b>FOREIGN</b> - acesso externo (terceiro/parceiro): somente leitura,
 *       e apenas do que e operacional. Nao ve dados pessoais nem usuarios.</li>
 * </ul>
 *
 * <h2>Niveis de criticidade</h2>
 * <table>
 *   <tr><th>Nivel</th><th>O que e</th><th>Quem acessa</th></tr>
 *   <tr><td>0 - Publico</td><td>login e renovacao de token</td><td>anonimo</td></tr>
 *   <tr><td>1 - Sessao propria</td><td>/auth/me, logout, troca da propria senha</td><td>autenticado</td></tr>
 *   <tr><td>2 - Leitura operacional</td><td>GET de projetos, restaurantes, alojamentos, equipamentos</td><td>autenticado</td></tr>
 *   <tr><td>3 - Escrita operacional</td><td>POST/PUT/PATCH desses mesmos recursos</td><td>ADMIN, USER</td></tr>
 *   <tr><td>4 - Dados pessoais</td><td>clientes, funcionarios, enderecos (LGPD)</td><td>ADMIN, USER</td></tr>
 *   <tr><td>5 - Critico</td><td>gestao de usuarios/perfis e toda exclusao</td><td>ADMIN</td></tr>
 * </table>
 *
 * Tudo que nao casa com nenhuma regra e negado ({@code denyAll}): endpoint novo
 * nasce fechado, e nao aberto por esquecimento.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties({JwtProperties.class, SecurityCorsProperties.class})
public class SecurityConfig {

    private static final String[] OPERATIONAL_RESOURCES = {
            "/api/v1/projects/**",
            "/api/v1/restaurants/**",
            "/api/v1/accommodations/**",
            "/api/v1/equipments/**"
    };

    private static final String[] PERSONAL_DATA_RESOURCES = {
            "/api/v1/clients/**",
            "/api/v1/employees/**",
            "/api/v1/addresses/**"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final SecurityCorsProperties corsProperties;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          RestAuthenticationEntryPoint authenticationEntryPoint,
                          RestAccessDeniedHandler accessDeniedHandler,
                          SecurityCorsProperties corsProperties) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.corsProperties = corsProperties;
    }

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // Despachos internos do container nao passam por autorizacao.
                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.ASYNC).permitAll()

                        // Nivel 0 - publico. Somente obter e renovar credencial.
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/refresh").permitAll()

                        // Nivel 5 - critico: gestao de usuarios e perfis.
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")

                        // Nivel 5 - critico: exclusao e sempre irreversivel.
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasRole("ADMIN")

                        // Nivel 1 - a propria sessao do usuario autenticado.
                        .requestMatchers("/api/v1/auth/**").authenticated()

                        // Nivel 4 - dados pessoais: fora do alcance de terceiros.
                        .requestMatchers(PERSONAL_DATA_RESOURCES).hasAnyRole("ADMIN", "USER")

                        // Nivel 3 - escrita operacional.
                        .requestMatchers(HttpMethod.POST, OPERATIONAL_RESOURCES).hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, OPERATIONAL_RESOURCES).hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PATCH, OPERATIONAL_RESOURCES).hasAnyRole("ADMIN", "USER")

                        // Nivel 2 - leitura operacional.
                        .requestMatchers(HttpMethod.GET, OPERATIONAL_RESOURCES).authenticated()

                        .anyRequest().denyAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Console do H2 fora da cadeia da API: ela e stateless e nega tudo que nao
     * conhece. Existe apenas no perfil de desenvolvimento.
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Profile("h2")
    public SecurityFilterChain h2ConsoleFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/h2-console/**")
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    /** BCrypt com custo 12: caro o suficiente para forca bruta, viavel no login. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
