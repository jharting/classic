package cz.muni.fi.xharting.classic.test.web;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet("/classic/test")
public class SimpleServlet extends HttpServlet {

    @Inject
    private InjectedBean injectedBean;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        if (!"mississippi".equals(injectedBean.getName())) {
            resp.setStatus(500);
            return;
        }
        if (12345 != injectedBean.getId())
        {
            resp.setStatus(500);
            return;
        }
        resp.setStatus(200);
        return;
    }

}
