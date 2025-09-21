package it.korea.app_boot.common.config;


import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.HttpStatusAccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.korea.app_boot.common.handler.LoginFailureHandler;
import it.korea.app_boot.common.handler.LoginSuccessHandler;
import it.korea.app_boot.common.handler.LogoutHandler;
import it.korea.app_boot.user.service.UserServiceDetails;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserServiceDetails serviceDetails;

    // 시큐리티 우선 무시하기
    // 스프링 시큐리티 필터 적용 아예 X
    // -> 보통 공개되도 상관없고, 필터 거치면 성능 저하의 가능성 존재
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> 
                web.ignoring()
                .requestMatchers("/static/img/**")
                // 스프링 리소스 관련 처리
                /*
                 * 1. classPath:/MATA-INF/resource/    // 라이브러리 리소스들 폴더
                 * 2. classPath:/resource/
                 * 3. classPath:/static/
                 * 4. classPath:/public/
                 */
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());  
    }

    // 보안처리
    /**
     * security 6 특징
     * 메서드 파라미터를 전부 함수형 인터페이스 처리
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            // 인증 / 비인증 경로 처리
            // 필터는 거치지만 인증은 안받아도 됨!
            // 누구나 접근 가능하게 명시적 허용하는 것
            .authorizeHttpRequests(
                auth ->
                    auth.requestMatchers("/user/login/**").permitAll()  // 인증 없이 접근 허용 >> 로그인체크 안해
                        .requestMatchers("/user/login/error").permitAll()
                        .requestMatchers("/user/logout/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/.well-known/**").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers(HttpMethod.GET, "/gal/**", "/api/v1/gal/**").permitAll()     // 조회만 하는거는 로그인 체크 안함
                        .requestMatchers(HttpMethod.GET, "/board/**", "/api/v1/board/**").permitAll()
                        .requestMatchers("/admin/**", "/api/v1/admin/**").hasAnyRole("ADMIN")  // 사용자가 admin권한이 있을때만 접근 허용
                        .anyRequest().authenticated()  // 이외의 모든것은 인증처리 하겠다
                    
            ) 
            // 로그인 처리
            .formLogin(
                form ->
                    form.loginPage("/user/login")  // 내가 만든 로그인 페이지 경로
                        .loginProcessingUrl("/login/proc")  // 로그인 처리 시작 경로
                        .successHandler(new LoginSuccessHandler())  // 성공 시
                        .failureHandler(new LoginFailureHandler())  // 실패 시
            ).logout(
                out ->
                    // 요청 URL이 /logout이면 이 요청은 로그아웃 요청임
                    out.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                       .invalidateHttpSession(true)    // spring session 제거
                       .deleteCookies("JSESSIONID")    // 세션 id 제거
                       .clearAuthentication(true)      // 로그인 객체 삭제
                       .logoutSuccessHandler(new LogoutHandler())   // 로그 아웃 후 처리
            ) 
            // API 요청에 대해서는 401 응답(리다이렉트 하지 않음) 
            // 비동기통신시(axios, ajax) 오류가 났을 때 메서드가 변경이 안됨 >> 단순 페이지 이동이면 이거 안해도 됨
            .exceptionHandling(
                // 비로그인 상태에서 api 호출 시 오류
                exp->
                    exp.defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")
                // 로그인이지만 권한 없는 api 호출 시 오류      
                // 403 코드 반환 
                ).defaultAccessDeniedHandlerFor(
                    new HttpStatusAccessDeniedHandler(HttpStatus.FORBIDDEN),
                    new AntPathRequestMatcher("/api/**"))
            );

        return http.build();
    }

    // auth provider 생성해서 전달 > 사용자가 만든것을 전달
    @Bean
    public AuthenticationProvider authProvider(){
        // 사용자 정보를 DB에서 가져와서 인증하는 역할
        // serviceDetails을 DaoAuthenticationProvider에 넘겨서 인증 시 사용자정보 가져오게 함
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(serviceDetails);
        // 비밀번호 비교시 암호화된 값끼리 비교함
        // PasswordEncoder를 꼭 설정해야 함
        provider.setPasswordEncoder(bcyPasswordEncoder());
        return provider;
    }

    // 패스워드 암호화 객체 설정
    // PasswordEncoder : 비밀번호를 암호화하고, 비교시 암호화된 값끼리 비교하는 역할을 함
    @Bean
    public PasswordEncoder bcyPasswordEncoder(){
        // 단방향 암호화 방식, 복호화 없음, 값 비교는 가능
        return new BCryptPasswordEncoder();
    }
}
