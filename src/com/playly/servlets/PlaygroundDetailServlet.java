package com.playly.servlets;

import com.playly.dao.PlaygroundDAO;
import com.playly.models.Playground;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/playground")
public class PlaygroundDetailServlet extends HttpServlet {
    private PlaygroundDAO playgroundDAO;

    @Override
    public void init() {
        playgroundDAO = new PlaygroundDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || !idParam.matches("\\d+")) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            Playground playground = playgroundDAO.getById(id);

            if (playground == null) {
                response.sendRedirect(request.getContextPath() + "/");
                return;
            }

            request.setAttribute("playground", playground);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/playground-detail.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException | NumberFormatException e) {
            throw new ServletException("Error loading playground", e);
        }
    }
}