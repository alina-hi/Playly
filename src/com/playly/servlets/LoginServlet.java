package com.playly.servlets;

import com.playly.dao.UserDAO;
import com.playly.models.User;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            // Чтение JSON из запроса
            Map<String, String> data = gson.fromJson(request.getReader(), Map.class);

            String login = data.get("login"); // может быть email или username
            String password = data.get("password");

            // Валидация
            if (login == null || login.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Введите логин и пароль");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            boolean isAuthenticated = false;
            User user = null;

            // Пытаемся определить, что ввел пользователь: email или username
            if (login.contains("@")) {
                // Похоже на email
                isAuthenticated = userDAO.checkPassword(login, password);
                if (isAuthenticated) {
                    Optional<User> userOpt = userDAO.getUserByEmail(login);
                    if (userOpt.isPresent()) {
                        user = userOpt.get();
                    }
                }
            } else {
                // Похоже на username
                isAuthenticated = userDAO.checkPasswordByUsername(login, password);
                if (isAuthenticated) {
                    Optional<User> userOpt = userDAO.getUserByUsername(login);
                    if (userOpt.isPresent()) {
                        user = userOpt.get();
                    }
                }
            }

            if (isAuthenticated && user != null) {
                // Проверяем, активен ли пользователь
                if (!user.isActive()) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    result.put("success", false);
                    result.put("message", "Аккаунт деактивирован");
                    response.getWriter().write(gson.toJson(result));
                    return;
                }

                // Создаем сессию
                HttpSession session = request.getSession();
                session.setAttribute("userId", user.getId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("email", user.getEmail());
                session.setAttribute("isVerified", user.isVerified());
                session.setAttribute("reputation", user.getReputation());

                // Устанавливаем время жизни сессии (30 минут)
                session.setMaxInactiveInterval(30 * 60);

                response.setStatus(HttpServletResponse.SC_OK);
                result.put("success", true);
                result.put("message", "Вход выполнен успешно");

                // Создаем user info map вручную для Java 8
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("username", user.getUsername());
                userInfo.put("email", user.getEmail());
                userInfo.put("isVerified", user.isVerified());
                userInfo.put("reputation", user.getReputation());
                userInfo.put("status", user.getStatusString());

                result.put("user", userInfo);

            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                result.put("success", false);
                result.put("message", "Неверный логин или пароль");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "Внутренняя ошибка сервера");
            e.printStackTrace();
        }

        response.getWriter().write(gson.toJson(result));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Проверка текущей сессии
        HttpSession session = request.getSession(false);
        Map<String, Object> result = new HashMap<>();

        if (session != null && session.getAttribute("userId") != null) {
            result.put("success", true);
            result.put("authenticated", true);

            // Создаем user info map вручную для Java 8
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", session.getAttribute("userId"));
            userInfo.put("username", session.getAttribute("username"));
            userInfo.put("email", session.getAttribute("email"));
            userInfo.put("isVerified", session.getAttribute("isVerified"));

            result.put("user", userInfo);
        } else {
            result.put("success", true);
            result.put("authenticated", false);
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(result));
    }
}