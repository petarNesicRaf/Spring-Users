package rs.raf.springusers.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import rs.raf.springusers.entities.Role;
import rs.raf.springusers.services.UserService;

import java.beans.JavaBean;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final UserService userService;
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        return taskScheduler;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request->request.requestMatchers("/api/v1/auth/**")
                        .permitAll()
                        .requestMatchers("/api/v1/vacuum/*/add-vacuum").hasAuthority(Role.CAN_ADD_VACUUM.name())
                        .requestMatchers("/api/v1/vacuum/turn-on").hasAuthority(Role.CAN_START_VACUUM.name())
                        .requestMatchers("/api/v1/vacuum/*/turn-on-scheduled").hasAuthority(Role.CAN_START_VACUUM.name())
                        .requestMatchers("/api/v1/vacuum/*/turn-off").hasAuthority(Role.CAN_STOP_VACUUM.name())
                        .requestMatchers("/api/v1/vacuum/*/turn-off-scheduled").hasAuthority(Role.CAN_STOP_VACUUM.name())
                        .requestMatchers("/api/v1/vacuum/*/discharge").hasAuthority(Role.CAN_DISCHARGE_VACUUM.name())
                        .requestMatchers("/api/v1/vacuum/*/discharge-scheduled").hasAuthority(Role.CAN_DISCHARGE_VACUUM.name())
                        .requestMatchers("/api/v1/vacuum/search").hasAuthority(Role.CAN_SEARCH_VACUUM.name())
                        .requestMatchers("/api/v1/vacuum/*/delete-vacuum").hasAuthority(Role.CAN_REMOVE_VACUUMS.name())
                        .requestMatchers("/api/v1/vacuum/*/get-all-vacuums").permitAll()
                        .requestMatchers("/api/v1/admin/get-users").hasAuthority(Role.CAN_READ.name())
                        .requestMatchers("/api/v1/admin/*/update-user").hasAuthority(Role.CAN_READ.name())
                        .requestMatchers("/api/v1/admin/*/delete-user").hasAuthority(Role.CAN_READ.name())
                        .requestMatchers("/api/v1/admin/create-user").hasAuthority(Role.CAN_CREATE.name())
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()).addFilterBefore(
                        jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userService.userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
