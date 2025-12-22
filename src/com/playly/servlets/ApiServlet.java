package com.playly.servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.playly.models.Playground;
import com.playly.dao.PlaygroundDAO;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class ApiServlet extends HttpServlet {
    private PlaygroundDAO dao = new PlaygroundDAO();
    private Gson gson;

    public ApiServlet() {
        // Настраиваем Gson для красивого вывода
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .setPrettyPrinting()
                .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            String servletPath = request.getServletPath();
            String pathInfo = request.getPathInfo();

            System.out.println("=== DEBUG INFO ===");
            System.out.println("Request URI: " + requestURI);
            System.out.println("Context Path: " + contextPath);
            System.out.println("Servlet Path: " + servletPath);
            System.out.println("Path Info: " + pathInfo);

            // Определяем какой эндпоинт запрошен
            if (requestURI.endsWith("/api/playgrounds")) {
                System.out.println("Handling: GET /api/playgrounds");
                List<Playground> playgrounds = dao.getAllPlaygrounds();
                System.out.println("Found " + playgrounds.size() + " playgrounds");
                out.print(gson.toJson(playgrounds));

            } else if (requestURI.endsWith("/api/amenities")) {
                System.out.println("Handling: GET /api/amenities");
                List<String> amenities = dao.getAllAmenities();
                out.print(gson.toJson(amenities));

            } else if (requestURI.matches(".*/api/\\d+$")) {
                // Паттерн: /api/123 (только цифры)
                String[] parts = requestURI.split("/");
                String idStr = parts[parts.length - 1];

                try {
                    int id = Integer.parseInt(idStr);
                    System.out.println("Handling: GET /api/" + id);
                    Playground playground = dao.getById(id);

                    if (playground != null) {
                        out.print(gson.toJson(playground));
                    } else {
                        response.setStatus(404);
                        out.print("{\"error\": \"Playground not found with id: " + id + "\"}");
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(400);
                    out.print("{\"error\": \"Invalid ID: " + idStr + "\"}");
                }

            } else if (requestURI.endsWith("/api/") || "/api".equals(servletPath)) {
                System.out.println("Handling: GET /api/ (root)");
                List<Playground> playgrounds = dao.getAllPlaygrounds();
                out.print(gson.toJson(playgrounds));

            } else {
                // Неизвестный путь
                System.out.println("Unknown path: " + requestURI);
                response.setStatus(404);
                out.print("{\"error\": \"Endpoint not found: " + requestURI + "\"}");
            }

        } catch (Exception e) {
            System.err.println("ERROR in ApiServlet: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(500);
            out.print("{\"error\": \"" + e.getClass().getSimpleName() + ": " + e.getMessage() + "\"}");
        }
    }
}