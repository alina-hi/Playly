package com.playly.servlets;

import com.playly.dao.PlaygroundDAO;
import com.playly.models.Playground;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

// ИЗМЕНИТЕ С "/" на "/main" или "/home"
@WebServlet("/map")
public class MainPageServlet extends HttpServlet {
    private PlaygroundDAO playgroundDAO;

    @Override
    public void init() {
        playgroundDAO = new PlaygroundDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Проверка авторизации
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            // Если не авторизован - редирект на страницу входа
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }
        try {
            // Получаем параметры фильтров
            String[] amenityFilters = request.getParameterValues("amenity");
            List<String> amenities = playgroundDAO.getAllAmenities();

            List<Playground> playgrounds;

            // Применяем фильтры, если есть
            if (amenityFilters != null && amenityFilters.length > 0) {
                List<String> filtersList = new ArrayList<>();
                for (String filter : amenityFilters) {
                    filtersList.add(filter);
                }
                playgrounds = playgroundDAO.filterByAmenities(filtersList);
            } else {
                playgrounds = playgroundDAO.getAllPlaygrounds();
            }

            // Передаем данные в JSP
            request.setAttribute("playgrounds", playgrounds);
            request.setAttribute("allAmenities", amenities);

            // Список выбранных фильтров
            List<String> selectedFilters = new ArrayList<>();
            if (amenityFilters != null) {
                selectedFilters = Arrays.asList(amenityFilters);
            }
            request.setAttribute("selectedAmenities", selectedFilters);

            // JSON для карты
            String jsonPlaygrounds = convertToJson(playgrounds);
            request.setAttribute("jsonPlaygrounds", jsonPlaygrounds);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/main.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }

    private String convertToJson(List<Playground> playgrounds) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < playgrounds.size(); i++) {
            Playground pg = playgrounds.get(i);
            json.append(String.format(
                    "{\"id\":%d,\"name\":\"%s\",\"address\":\"%s\",\"lat\":%.6f,\"lng\":%.6f}",
                    pg.getId(),
                    escapeJson(pg.getName()),
                    escapeJson(pg.getAddress()),
                    59.93 + Math.random() * 0.05,
                    30.31 + Math.random() * 0.05
            ));
            if (i < playgrounds.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}