package cs137;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import javax.sql.*;
import java.util.ArrayDeque;
import java.util.HashMap;


@WebServlet(urlPatterns = {"/product"})
public class product extends HttpServlet { 
    Connection conn = null;
    Statement  stmt = null;
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
        
        if ((ArrayDeque<String>) request.getSession().getAttribute("deque") == null){
            //deque = new ArrayDeque<>();
            request.getSession().setAttribute("deque", new ArrayDeque<String>());
        }
     
        try {
            PrintWriter out = response.getWriter();

            if (!((ArrayDeque<String>) request.getSession().getAttribute("deque")).contains((String)request.getParameter("pid")))
                ((ArrayDeque<String>) request.getSession().getAttribute("deque")).add((String)request.getParameter("pid"));
            if (((ArrayDeque<String>) request.getSession().getAttribute("deque")).size() > 5)
                ((ArrayDeque<String>) request.getSession().getAttribute("deque")).pop();
            /* Including .css file and setting the title */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"detail.css\" />");
            out.println("<title> Product </title>");            
            out.println("</head>");
            out.println("<body>");
            
            // include our menu bar
            request.getRequestDispatcher("WEB-INF/header.html").include(request, response);
            
            // content
            out.println("<div>");
            synchronized(mutex){
                String currentProductId = request.getParameter("pid");
                stmt = conn.createStatement();
                rs = stmt.executeQuery("select * from product where pid="+"'"+currentProductId+"'");
                
                if(rs.next()){
                    
                    if (request.getSession().getAttribute("lastVisitedProductId") == null)
                    {
                        request.getSession().setAttribute("lastVisitedProductId",currentProductId);
                        HashMap<String, Integer>viewcounter = (HashMap) getServletContext().getAttribute("viewcounter");
                        if(viewcounter == null ){
                            //viewcounter = new HashMap<String, Integer>();
                            getServletContext().setAttribute("viewcounter", new HashMap<String,Integer>());
                        }
                        Integer count;// = ((HashMap) getServletContext().getAttribute("viewcounter")).containsKey("phone_iphone")?((HashMap) getServletContext().getAttribute("viewcounter")).get("phone_iphone"):0;
                        if (((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).containsKey(currentProductId))
                        {
                            count = ((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).get(currentProductId);
                        } else
                        {
                            count = 0;             
                        }
                        ((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).put(currentProductId,count+1);
                    } else if (((String)request.getSession().getAttribute("lastVisitedProductId")).equals(currentProductId))
                    {
                        // do nothing
                    } else
                    {
                        // lastVisitedId is not null
                        Integer count1;
                        if (((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).containsKey((String)request.getSession().getAttribute("lastVisitedProductId")))
                        {
                            count1 = ((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).get((String)request.getSession().getAttribute("lastVisitedProductId"));
                        } else
                        {
                            // Critical Error   
                            out.println("Visited other product but the item in HashMap does not exist");
                            count1 = 0;
                        }
                        ((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).put((String)request.getSession().getAttribute("lastVisitedProductId"),count1-1);
                        request.getSession().setAttribute("lastVisitedProductId",currentProductId);

                        Integer count;
                        if (((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).containsKey(currentProductId))
                        {
                            count = ((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).get(currentProductId);
                        } else
                        {
                            count = 0;             
                        }
                        ((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).put(currentProductId,count+1);

                    }
                    
                    String name = rs.getString("name");
                    String pid = rs.getString("pid");
                    float price = rs.getFloat("price");
                    String types = rs.getString("types");
                    String long_title = rs.getString("long_title");
                    String description = rs.getString("description");
                    
                    out.println("<div>");
                        out.println("<div class = 'productimage'>");
                            out.print("<br>");
                            out.println("<img src='picture/"+types+"/"+name+"/"+name+"_main.jpg' alt='"+name+ "/" + "width='200' height='200'" +"'/>");
                            out.print("<br>");
                            out.println("<img src='picture/"+types+"/"+name+"/"+name+"_1.jpg' alt='"+name+ "/" + "width='200' height='200'" +"'/>");
                            out.print("<br>");
                            out.println("<img src='picture/"+types+"/"+name+"/"+name+"_2.jpg' alt='"+name+ "/" + "width='200' height='200'" +"'/>");
                            out.print("<br>");
                            out.println("<img src='picture/"+types+"/"+name+"/"+name+"_3.jpg' alt='"+name+ "/" + "width='200' height='200'" +"'/>");
                            out.print("<br>");
                        out.println("</div>");
                    
                        out.println("<div class = 'productdescription'>");
                    
                            out.println("<div class = 'fontproductname'>");
                                out.print("<br>");
                                out.print(long_title);
                                out.print("<br>");
                            out.println("</div>");

                            out.println("<div class='fontproductdescription'>");
                                out.println("<b><font color = '696969'>Price: </font></b>" + price);
                                out.println("<br>");
                                out.println("<b><font color = '696969'>Product Identifier: </font></b>" + pid);
                                out.println("<br>");
                                
                                for (String des: description.split(";")){
                                    out.println(des);
                                    out.print("<br>");
                                }

                            out.println("</div><br/>");

                            out.println("<form action='addingToCart' method = 'post' />");
                            out.println("<input type='hidden' name='pid' value='"+pid+"'/>" );
                            
                            out.println("<input class='form-button' type='submit' value='Add to cart'/>");

                            out.println(String.valueOf(((HashMap<String, Integer>) getServletContext().getAttribute("viewcounter")).get(currentProductId))+" users are viewing this product");
            
                            
                        out.println("</div>");
                out.println("</div>");
                }
            }

            out.println("</div>");
            out.println("<div class='footer_for_detail'>");
            request.getRequestDispatcher("WEB-INF/footer.html").include(request, response);
            out.println("</div>");
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            synchronized(mutex){
                if(stmt != null){
                    stmt.close();
                }
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
