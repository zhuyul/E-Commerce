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

@WebServlet(name = "zip", urlPatterns = {"/zip"})
public class zip extends HttpServlet {
    Connection conn = null;
    Statement  stmt = null;
    PreparedStatement preparedStmt = null;
    ResultSet   rs = null;
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
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        try{
            synchronized(mutex){
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT 1");
                rs.close();
                stmt.close();
            }
        }catch(SQLException e){
            try{
                synchronized(mutex){
                    Class.forName("com.mysql.jdbc.Driver");
                    conn = DriverManager.getConnection(DbAccess.Dblocation,DbAccess.AccountID,DbAccess.Password);
                }
            }catch(Exception x){
                x.printStackTrace();
            }
        }
        try (PrintWriter out = response.getWriter()) {
            //String zipcode = request.getParameter("zipcode");
            synchronized(mutex){
                preparedStmt = conn.prepareStatement("SELECT State, City FROM zipcode WHERE Zipcode=?");//INSERT INTO orders (fname, lname, phone_number, shipping_address, shipping_method, credit_number, credit_name, credit_month, credit_year, credit_cvc, shipping_state, shipping_zip, shipping_city, pid, quantity, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
                preparedStmt.setString(1, (String)request.getParameter("zipcode"));
                rs = preparedStmt.executeQuery(); 
                if(rs.next()){
                    out.println("[\""+rs.getString("State")+"\",\""+rs.getString("City")+"\"]");
                } else{
                    out.println("['','']");    
                }
            }
        }
    }

    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try{
            processRequest(request, response);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try{
            processRequest(request, response);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
