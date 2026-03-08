package org.example.studyhub.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.studyhub.annotation.RequireRole;
import org.example.studyhub.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            session.setAttribute("interceptedError", "Bạn cần đăng nhập để truy cập trang này.");
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return false;
        }


        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
            if (requireRole == null) {
                requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
            }

            if (requireRole != null) {

                List<String> userRoles = user.getUserRoles().stream()
                        .map(ur -> ur.getRole().getName())
                        .toList();

                boolean hasPermission = false;
                for (String requiredRole : requireRole.value()) {
                    if (userRoles.contains(requiredRole)) {
                        hasPermission = true;
                        break;
                    }
                }

                if (!hasPermission) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập chức năng này.");
                    return false;
                }
            }
        }

        return true;
    }
}