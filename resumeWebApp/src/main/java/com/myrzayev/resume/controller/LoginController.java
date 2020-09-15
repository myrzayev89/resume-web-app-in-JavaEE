package com.myrzayev.resume.controller;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.myrzayev.daoInter.UserDaoInter;
import com.myrzayev.entity.User;
import com.myrzayev.main.Context;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    private UserDaoInter userDao = Context.instanceUserDao();
    private BCrypt.Verifyer verifyer = BCrypt.verifyer();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String username = req.getParameter("username");
            String password = req.getParameter("password");
            User user = userDao.findByUsername(username);

            if (user == null) {
                throw new IllegalArgumentException("User doesn't exist!");
            }
            BCrypt.Result rs = verifyer.verify(password.toCharArray(), user.getPassword().toCharArray());
            if (!rs.verified) {
                throw new IllegalArgumentException("Password incorrect!");
            }
            req.getSession().setAttribute("loggedInUser", user);
            resp.sendRedirect("index");
        } catch (Exception ex) {
            ex.printStackTrace();
            resp.sendRedirect("error?msg=" + ex.getMessage());
        }
    }
}
