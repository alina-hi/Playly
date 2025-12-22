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

@WebServlet("/api/profile/*")
public class ProfileServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            String pathInfo = request.getPathInfo();

            // Если запрос без ID - возвращаем текущий профиль
            if (pathInfo == null || pathInfo.equals("/")) {
                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("userId") == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    result.put("success", false);
                    result.put("message", "Требуется авторизация");
                    response.getWriter().write(gson.toJson(result));
                    return;
                }

                int userId = (int) session.getAttribute("userId");
                Optional<User> userOpt = userDAO.getUserById(userId);

                if (userOpt.isPresent()) {
                    result.put("success", true);
                    result.put("user", userToMap(userOpt.get()));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    result.put("success", false);
                    result.put("message", "Пользователь не найден");
                }

            } else {
                // Запрос конкретного профиля по ID
                try {
                    int userId = Integer.parseInt(pathInfo.substring(1));
                    Optional<User> userOpt = userDAO.getUserById(userId);

                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        // Не возвращаем чувствительные данные для чужих профилей
                        Map<String, Object> publicProfile = new HashMap<>();
                        publicProfile.put("id", user.getId());
                        publicProfile.put("username", user.getUsername());
                        publicProfile.put("displayName", user.getDisplayName());
                        publicProfile.put("isVerified", user.isVerified());
                        publicProfile.put("reputation", user.getReputation());
                        publicProfile.put("about", user.getAbout());
                        publicProfile.put("childAgeRange", user.getChildAgeRange());
                        publicProfile.put("interests", user.getInterests());
                        publicProfile.put("location", user.getLocation());
                        publicProfile.put("avatarUrl", user.getAvatarUrl());
                        publicProfile.put("createdAt", user.getCreatedAt());
                        publicProfile.put("status", user.getStatusString());
                        publicProfile.put("childInfo", user.getChildInfo());

                        result.put("success", true);
                        result.put("user", publicProfile);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        result.put("success", false);
                        result.put("message", "Пользователь не найден");
                    }

                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    result.put("success", false);
                    result.put("message", "Некорректный ID пользователя");
                }
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
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> result = new HashMap<>();

        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                result.put("success", false);
                result.put("message", "Требуется авторизация");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            int userId = (int) session.getAttribute("userId");
            Optional<User> userOpt = userDAO.getUserById(userId);

            if (!userOpt.isPresent()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                result.put("success", false);
                result.put("message", "Пользователь не найден");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            // Чтение данных профиля из JSON
            Map<String, String> data = gson.fromJson(request.getReader(), Map.class);
            User user = userOpt.get();

            // Обновляем только разрешенные поля
            if (data.containsKey("displayName")) {
                user.setDisplayName(data.get("displayName").trim());
            }

            if (data.containsKey("about")) {
                user.setAbout(data.get("about").trim());
            }

            if (data.containsKey("childAgeRange")) {
                user.setChildAgeRange(data.get("childAgeRange").trim());
            }

            if (data.containsKey("interests")) {
                user.setInterests(data.get("interests").trim());
            }

            if (data.containsKey("location")) {
                user.setLocation(data.get("location").trim());
            }

            if (data.containsKey("avatarUrl")) {
                user.setAvatarUrl(data.get("avatarUrl").trim());
            }

            // Валидация
            if (user.getDisplayName() != null && user.getDisplayName().trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                result.put("success", false);
                result.put("message", "Отображаемое имя не может быть пустым");
                response.getWriter().write(gson.toJson(result));
                return;
            }

            boolean updated = userDAO.updateProfile(user);

            if (updated) {
                // Обновляем данные в сессии
                session.setAttribute("username", user.getUsername());
                session.setAttribute("displayName", user.getDisplayName());

                result.put("success", true);
                result.put("message", "Профиль обновлен");
                result.put("user", userToMap(user));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                result.put("success", false);
                result.put("message", "Ошибка при обновлении профиля");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            result.put("success", false);
            result.put("message", "Внутренняя ошибка сервера");
            e.printStackTrace();
        }

        response.getWriter().write(gson.toJson(result));
    }

    // Вспомогательный метод для преобразования User в Map
    private Map<String, Object> userToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("email", user.getEmail());
        userMap.put("username", user.getUsername());
        userMap.put("displayName", user.getDisplayName());
        userMap.put("isVerified", user.isVerified());
        userMap.put("reputation", user.getReputation());
        userMap.put("about", user.getAbout());
        userMap.put("childAgeRange", user.getChildAgeRange());
        userMap.put("interests", user.getInterests());
        userMap.put("location", user.getLocation());
        userMap.put("avatarUrl", user.getAvatarUrl());
        userMap.put("createdAt", user.getCreatedAt());
        userMap.put("isActive", user.isActive());
        userMap.put("status", user.getStatusString());
        userMap.put("childInfo", user.getChildInfo());

        return userMap;
    }
}