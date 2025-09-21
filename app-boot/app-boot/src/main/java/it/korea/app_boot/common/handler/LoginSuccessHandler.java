package it.korea.app_boot.common.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import it.korea.app_boot.user.dto.UserSecuDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{

    // http 요청을 기억하는 객체
    // 로그인하기 전 사용자가 요청했던 원래 URL 주소를 기억해두는 객체
    private RequestCache requestCache = new HttpSessionRequestCache();
    
    // 응답 전략
    // 로그인 성공 후 사용자를 원하는 주소로 보내는 방법을 담당하는 객체
    // 그럼 requestCache가 기억해둔 곳으로 redirectStrategy 가 보내줌
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    // setter를 통한 의존성 주입
    // 선택적 의존성 주입위해 >> 외부에서 의존성 주입 가능
    // 생성자를 통한 주입을 하면 무조건 꼭 주입해야 하며, 불변성임
    public void setRequestCache(RequestCache requestCache){
        this.requestCache = requestCache;
    }

    // 권한 처리 후 실행
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {

        // 로그인 성공 후 처리
        // 기본적으로 저 경로로 보내줌
        setDefaultTargetUrl("/board/list");

        // 이동경로가 있으면 우선사용
        setAlwaysUseDefaultTargetUrl(false);
        
        // 세션 유지기간 설정
        // 로그인 세션을 30분(1800초)동안 유지시켜줄게
        request.getSession().setMaxInactiveInterval(1800);

        // 원래 가려던 페이지가 있으면 거기로 보내주기
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        // 세션 가져오기
        HttpSession session = request.getSession();
        // 사용자 정보 저장
        session.setAttribute("user", (UserSecuDTO)authentication.getPrincipal());

        // 이전에 저장된 요청 경로가 있을 경우 처리
        if(savedRequest != null){
            String targetURI = savedRequest.getRedirectUrl();

            // 오류나서 넘어오는 경우 디폴트 경로 줌
            if(targetURI.contains("error") || targetURI.contains(".well-known")
               || targetURI.contains("login")) {
                targetURI = getDefaultTargetUrl();
            }
            // 최종적으로 보내주기(요청경로)
            redirectStrategy.sendRedirect(request, response, targetURI);

        }else{
            // 이전에 요청한 경로 없으면 디폴트 경로로 보내기
            redirectStrategy.sendRedirect(request, response, getDefaultTargetUrl());
        }
    }
}
