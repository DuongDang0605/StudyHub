package org.example.studyhub.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();

        if (session.getAttribute("loggedInUser") == null) {
            session.setAttribute("interceptedError", "Bạn cần đăng nhập để truy cập trang này.");

            response.sendRedirect(request.getContextPath() + "/auth/login");

            return false;
        }

        return true;
    }
}