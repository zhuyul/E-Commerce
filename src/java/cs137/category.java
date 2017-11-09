package cs137;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(urlPatterns = {"/category"})
public class category extends HttpServlet {
    
    Connection conn = null;
    Statement  stmt = null;
    ResultSet   rs = null;
    String sql = "";
    String mutex = "";
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    // Always call super.init(config) first  (servlet mantra #1)
        super.init(config);
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DbAccess.Dblocation,DbAccess.AccountID,DbAccess.Password);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        }
    }
    @Override
    public void destroy() {
        try{
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try{
            processRequest(request,response);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try{
            processRequest(request,response);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        try{
            synchronized(sql){
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT 1");
                rs.close();
                stmt.close();
            }
        }catch(SQLException e){
            try{
                synchronized(sql){
                    Class.forName("com.mysql.jdbc.Driver");
                    conn = DriverManager.getConnection(DbAccess.Dblocation,DbAccess.AccountID,DbAccess.Password);
                }
            }catch(Exception x){
                x.printStackTrace();
            }
        }
        
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
                      
            // content
            //String uri = request.getRequestURI();
            //int subindex = uri.indexOf( "/", 1 );
            out.println("<div>");
            out.println("<table class = 'center'>");
            synchronized(sql){
                stmt = conn.createStatement();
                int loop = 1;
                rs = stmt.executeQuery("SELECT id, name,price,pid, types,full_name FROM product");
                while(rs.next()){
                    String param_types = request.getParameter("types");
                    String name = rs.getString("name");
                    String pid = rs.getString("pid");
                    float price = rs.getFloat("price");
                    String types = rs.getString("types");
                    String full_name = rs.getString("full_name");
                    String href = "href='product?pid="+pid+"'>";

                    if (param_types.equals(types)){
                        if (loop %3 == 1)
                            out.println("<tr>");
                        out.println("<th>");
                        out.println("<div class = 'img'>");
                        out.println("<a " + href);
                        out.println("<img src='picture/"+types+"/"+name+"/"+name+"_main.jpg' alt='"+name+"'/>");
                        out.println("<p> $"+price+"</p>");
                        out.println("<p>"+full_name+"</p>");
                        out.println("</a>");
                        out.println("</div>"); 
                        out.println("</th>");
                        if (loop %3 == 0)
                            out.println("</tr>");
                        loop++;
                    }
                }
            }
            out.println("</table>");
            out.println("</div>");
            
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
            synchronized(sql){
                if(stmt != null){
                    stmt.close();
                }
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
