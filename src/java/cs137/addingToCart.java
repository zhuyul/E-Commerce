/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet(name = "addingToCart", urlPatterns = {"/addingToCart"})
public class addingToCart extends HttpServlet {
    
    Connection conn = null;
    Statement  stmt = null;
    ResultSet   rs = null;
    String mutex = "";
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    // Always call super.init(config) first  (servlet mantra #1)
        super.init(config);
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(DbAccess.Dblocation,DbAccess.AccountID,DbAccess.Password);
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e){
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
        


        if ((shoppingCart) request.getSession().getAttribute("shoppingCart") == null){
            request.getSession().setAttribute("shoppingCart", new shoppingCart());
        }
        
        //String pid = request.getParameter("pid");
        //String remove = request.getParameter("remove");
        
        if((String)request.getParameter("remove") != null){
            ((shoppingCart) request.getSession().getAttribute("shoppingCart")).deleteproduct((String)request.getParameter("remove"));
        }else if ((String)request.getParameter("pid") != null){
            ((shoppingCart) request.getSession().getAttribute("shoppingCart")).addproduct((String)request.getParameter("pid"));
        }
       
        
        try(PrintWriter out = response.getWriter()){
            
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />");
            out.println("<title> Shopping Cart </title>");            
            out.println("</head>");
            out.println("<body>");
            request.getRequestDispatcher("WEB-INF/header.html").include(request, response);
            
            synchronized(mutex){
                double totoal_price = 0 ;
                
                for(String key: ((shoppingCart) request.getSession().getAttribute("shoppingCart")).getCartItems().keySet()){
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery("select * from product where pid="+"'"+key+"'");
                    if(rs.next()){
                        String name = rs.getString("name");
                        float single_price = rs.getFloat("price");
                        String types = rs.getString("types");
                        String long_title = rs.getString("long_title");
                        
                        int quantity = ((shoppingCart) request.getSession().getAttribute("shoppingCart")).getCartItems().get(key);
                        
                        totoal_price += single_price*quantity;
                        

                        
                        String str_quantity = Integer.toString(quantity);
                        
                        out.println("<div style='height:200px;padding-left: 10%;padding-right: 15%; padding-top: 20px'>");
                        
                        out.println("<div style=\"background-color:#FFFFFF; width:200px; height:200px; float:left; text-align:center\">");
                            out.println("<img src='picture/"+types+"/"+name+"/"+name+"_main.jpg' alt='"+name+ "' width='150px' height='150px'" +"'/>");                                               
                        out.println("</div>");
                        
                        
                        out.println("<div>");
                        
                        out.print("<font size = \"3\">");
                            out.print(long_title);
                        out.print("</font>");
                        

                        out.println("<div>");
                            out.println("<b>Price </b>" );
                            out.println("<font size=\"5\" style=\"position: absolute; right: 20%;\">" + single_price+ "</font>" );
                        out.println("</div>");
                        
                        out.println("<div>");
                            out.println("<b> Quantity </b>");
                            out.println("<input class='input_quantity' type='number' style=\" right: 20%; position: absolute;\" name='input_quantity' min='1' max='99' value='" + str_quantity +"'>");
                            out.println("</input>");
                            out.println("<input class='product_id' type='hidden' name='product_id' value='" + key +"'>");
                            out.println("</input>");
                            out.println("<input class='single_price' type='hidden' name='single_price' value='" + Float.toString(single_price) +"'>");
                            out.println("</input>");
                        out.println("</div>");
                        
                        out.println("<div style=\"padding-top:25px\">");
                            String uri_self = request.getRequestURI();
                            
                            String href_self = "href='"+uri_self+"?remove="+key+"'";
                            out.println("<a " + href_self + "class='form-button remove_button'> Remove Item </a>");
                        out.println("</div>");
                        
                        
                        
                        out.println("</div>");                       
                    }
                    out.println("</div>");
                    
                }
                
                
                out.println("<div style='padding-left: 15%;padding-right: 15%; padding-bottom: 25px;'>");
                    if( (String)request.getParameter("pid") == null){
                         out.println("<p style='text-align:center;'> Welcome to your shopping cart </p>");
                    }
                    else{
                        out.println("<p style='text-align:center;'>You have successfully add "+ (String)request.getParameter("pid") +"</p>");
                    }
                    out.println("<script src=\"jQuery.js\"></script>");
                    out.println("<script src=\"ajax.js\"></script>");
                    out.println("<b> Total Price:  $</b>");
                    out.println("<b id='new_price' style='color:#DC143C;font-size:20px;'>");
                    out.println(Math.round(totoal_price*100.0)/100.0 +"</b>"); //change the price here
                    out.println("<input id='total_price' type='hidden' name='total_price' value='" +Float.toString((float)(Math.round(totoal_price*100.0)/100.0)) +"'>");
                    out.println("</input>");
                    

                    String uri = request.getRequestURI();
                    int subindex = uri.indexOf( "/", 1 );
                    String href; 
                    if ( ((shoppingCart) request.getSession().getAttribute("shoppingCart")).getCartItems().isEmpty() ){
                        href = "href='"+uri.substring(0,subindex+1)+"intermediate.html'";
                    } else {
                        href = "href='"+uri.substring(0,subindex+1)+"checkout'";
                    }
                    out.println("<a "+ href +"class='form-button make_order'> Make order </a>");
                    if((String)request.getParameter("pid") != null){
                        String href_pid = "href='"+uri.substring(0,subindex+1)+"product?pid="+(String)request.getParameter("pid")+"'";
                        out.println(" <a "+ href_pid +"class='form-button go_back'> Continue Shopping </a>");
                    }
                    else{
                        String href_home = "href='"+uri.substring(0,subindex+1)+"home'";
                        out.println(" <a "+ href_home +"class='form-button go_back'> Continue Shopping </a>");
                    }
                    

                out.println("</div>");
            }
          
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
        }
        
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(addingToCart.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(addingToCart.class.getName()).log(Level.SEVERE, null, ex);
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
