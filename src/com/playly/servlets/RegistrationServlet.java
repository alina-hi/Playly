package com.playly.servlets;

import com.playly.dao.UserDAO;
import com.playly.models.User;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

@WebServlet("/api/register")
public class RegistrationServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final Gson gson = new Gson();

    // Метод для хеширования пароля с помощью SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            // Если SHA-256 недоступен, используем простой fallback
            System.err.println("SHA-256 algorithm not found, using simple hash");
            return Integer.toHexString(password.hashCode());
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            // Чтение JSON из запроса
            Map<String, String> data = gson.fromJson(request.getReader(), Map.class);

            String email = data.get("email");
            String username = data.get("username");
            String password = data.get("password");

            // Валидация
            if (email == null || email.trim().isEmpty() ||
                    username == null || username.trim().isEmpty() ||
                    password == null || password.trim().isEmpty()) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Все поля должны быть заполнены");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Проверка email
            if (!email.contains("@") || !email.contains(".")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Введите корректный email адрес");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Проверка username
            if (username.length() < 3) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Имя пользователя должно содержать не менее 3 символов");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            if (!username.matches("^[a-zA-Z0-9_]+$")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Имя пользователя может содержать только буквы, цифры и подчеркивание");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Проверка пароля
            if (password.length() < 6) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Пароль должен содержать не менее 6 символов");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Проверка существования email
            if (userDAO.emailExists(email)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                result.put("success", false);
                result.put("message", "Пользователь с таким email уже существует");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Проверка существования username
            if (userDAO.usernameExists(username)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                result.put("success", false);
                result.put("message", "Имя пользователя уже занято");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Хеширование пароля с помощью SHA-256
            String passwordHash = hashPassword(password);

            // Создание пользователя
            User user = new User(email.trim(), username.trim(), passwordHash);
            boolean created = userDAO.createUser(user);

            if (created) {
                // Получаем созданного пользователя с ID (используем Optional)
                Optional<User> userOptional = userDAO.getUserByEmail(email);

                if (userOptional.isPresent()) {
                    User createdUser = userOptional.get();

                    // Создаем сессию для автоматической авторизации
                    HttpSession session = request.getSession();
                    session.setAttribute("user", createdUser);
                    session.setAttribute("userId", createdUser.getId());
                    session.setAttribute("username", createdUser.getUsername());
                    session.setAttribute("email", createdUser.getEmail());
                    session.setMaxInactiveInterval(30 * 60); // 30 минут

                    response.setStatus(HttpServletResponse.SC_CREATED);
                    result.put("success", true);
                    result.put("message", "Регистрация успешна! Вы автоматически вошли в систему.");
                    result.put("userId", createdUser.getId());
                    result.put("username", createdUser.getUsername());
                    result.put("email", createdUser.getEmail());
                    result.put("sessionId", session.getId());
                    result.put("redirectTo", "profile.html"); // Указываем куда перенаправить
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    result.put("success", false);
                    result.put("message", "Ошибка при получении данных пользователя");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.put("success", false);
                result.put("message", "Ошибка при создании пользователя");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "Внутренняя ошибка сервера");
            e.printStackTrace();
        }

        response.getWriter().write(gson.toJson(result));
    }
}

