package it.korea.app_boot.common.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LogoutHandler implements LogoutSuccessHandler{

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        // 세션 가져오기
        HttpSession session = request.getSession();
        // 사용자 정보 지우기
        if(session.getAttribute("user") != null){
            session.removeAttribute("user");
        }

        // 쿠키정보 지우기
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            // 시간을 0으로해서 삭제 >> 브라우저는 즉시 그 쿠키를 삭제한다는 의미임
            for(Cookie cookie : cookies){
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect("/login/form");


    }

    
}
