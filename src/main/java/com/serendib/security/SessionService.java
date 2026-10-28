package com.serendib.security;

import com.serendib.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

/**
 * Handles user session tracking, login, logout, and role authorization.
 */
@Service
public class SessionService {
    public static final String SESSION_USER_KEY = "serendib_user";

    public void login(HttpSession session, User user) {
        if (session != null) {
            session.setAttribute(SESSION_USER_KEY, user);
        }
    }

    public User getCurrentUser(HttpSession session) {
        if (session == null) return null;
        Object obj = session.getAttribute(SESSION_USER_KEY);
        return (obj instanceof User) ? (User) obj : null;
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_USER_KEY);
            try {
                session.invalidate();
            } catch (IllegalStateException ignored) {
            }
        }
    }

    public boolean isAdmin(HttpSession session) {
        User user = getCurrentUser(session);
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }
}
