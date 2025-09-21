package it.korea.app_boot.common.handler;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFailureHandler implements AuthenticationFailureHandler{

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        
        // 한글 깨짐 방지
        String encoded = URLEncoder.encode("아이디 또는 패스워드를 확인해주십시오.","UTF-8");     
        response.sendRedirect("/user/login/error?msg=" + encoded);        
    }

}
