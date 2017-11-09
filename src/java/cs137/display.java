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

@WebServlet(urlPatterns = {"/display"})
public class display extends HttpServlet {
    
    Connection conn = null;
    Statement  stmt = null;
    //PreparedStatement preparedStmt = null;
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
        
        try {
            PrintWriter out = response.getWriter();
            //String uri = request.getRequestURI();
            
            //int subindex = uri.indexOf( "/", 1 );
            // content
            out.println("<div>");
            out.println("<table class = 'center'>");
            synchronized(sql){
                stmt = conn.createStatement();
                
                rs = stmt.executeQuery("SELECT id, name,price,pid, types,full_name FROM product");
                while(rs.next()){
                    int id  = rs.getInt("id");
                    String name = rs.getString("name");
                    String pid = rs.getString("pid");
                    float price = rs.getFloat("price");
                    String types = rs.getString("types");
                    String full_name = rs.getString("full_name");
                    if (id %3 == 1)
                        out.println("<tr>");
                    out.println("<th>");
                    out.println("<div class = 'img'>");
                    String href = "href='product?pid="+pid+"'>";
                    out.println("<a " + href);
                    out.println("<img src='picture/"+types+"/"+name+"/"+name+"_main.jpg' alt='"+name+"'/>");
                    out.println("<p> $"+price+"</p>");
                    out.println("<p>"+full_name+"</p>");
                    out.println("</a>");
                    out.println("</div>"); 
                    out.println("</th>");
                    if (id %3 == 0)
                        out.println("</tr>");
                }
            }
            out.println("</table>");
            out.println("</div>");
            

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
