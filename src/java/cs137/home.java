package cs137;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
/**
 *
 * @author Asher
 */
@WebServlet(urlPatterns = {"/home"})
public class home extends HttpServlet {

    String mutex = "";
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    // Always call super.init(config) first  (servlet mantra #1)
        super.init(config);
    }
    @Override
    public void destroy() {
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request,response);
        
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request,response);
        
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try {
            PrintWriter out = response.getWriter();
            /* Including .css file and setting the title */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />");
            out.println("<title> Group 20 </title>");            
            out.println("</head>");
            out.println("<body>");
            
            // include our menu bar
            request.getRequestDispatcher("WEB-INF/header.html").include(request, response);
            request.getRequestDispatcher("display").include(request, response);
            request.getRequestDispatcher("session").include(request, response);
            // include the footer, close body tag, close html
            request.getRequestDispatcher("WEB-INF/footer.html").include(request, response);
            if (request.getSession().getAttribute("lastVisitedProductId") != null){
                if (((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).containsKey((String)request.getSession().getAttribute("lastVisitedProductId")))
                {
                    synchronized(mutex){
                        Integer count1 = ((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).get((String)request.getSession().getAttribute("lastVisitedProductId"));
                        ((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).put((String)request.getSession().getAttribute("lastVisitedProductId"),count1-1);
                    }
                }
                request.getSession().setAttribute("lastVisitedProductId",null);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
