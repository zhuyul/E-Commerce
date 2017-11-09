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
import javax.servlet.http.HttpSession;
import java.util.ArrayDeque;


@WebServlet(urlPatterns = {"/session"})
public class session extends HttpServlet {
    
    Connection conn = null;
    Statement  stmt = null;
    ResultSet   rs = null;            

    String sql = "";
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    // Always call super.init(config) first  (servlet mantra #1)
        super.init(config);
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://sylvester-mccoy-v3.ics.uci.edu/inf124grp20","inf124grp20","f=adra5R");
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
        
        if ((ArrayDeque<String>) request.getSession().getAttribute("deque") == null){
            //deque = new ArrayDeque<>();
            request.getSession().setAttribute("deque", new ArrayDeque<>());
        }

        
        try {
            PrintWriter out = response.getWriter();
            // content
            out.println("<p align='center'>Recently viewed products: \n</P>");
            out.println("<table align = 'center'>");
            out.println("<tr>");
            for (String item : (ArrayDeque<String>) request.getSession().getAttribute("deque")){
                out.println("<th>");
                synchronized(sql){
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery("select name, types from product where pid="+"'"+item+"'");

                    if(rs.next()){
                        String name = rs.getString("name");
                        String types = rs.getString("types");
                        out.println("<a href='product?pid="+item+"'>");
                        out.println("<img src='picture/"+types+"/"+name+"/"+name+"_main.jpg' alt='"+name+ "/" + "width='200' height='200'" +"'/>");
                    }
                }
                out.println("</a>");
		out.println("</th>");
            }
            out.println("</tr>");
            out.println("</table>");
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
