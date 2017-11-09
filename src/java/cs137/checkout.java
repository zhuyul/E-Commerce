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
import java.util.Calendar;
import java.util.HashMap;


@WebServlet(urlPatterns = {"/checkout"})
public class checkout extends HttpServlet {
    
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
        try{
            processRequest(request,response);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    protected boolean is_zip_valid(String a){
        synchronized(mutex){
            try{
                preparedStmt = conn.prepareStatement("SELECT Zipcode FROM zipcode WHERE Zipcode=?");//INSERT INTO orders (fname, lname, phone_number, shipping_address, shipping_method, credit_number, credit_name, credit_month, credit_year, credit_cvc, shipping_state, shipping_zip, shipping_city, pid, quantity, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
                preparedStmt.setString(1, a);
                rs = preparedStmt.executeQuery();
                if (rs.next()){
                    return true;
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
        return false;
    }
    protected boolean is_form_valid(HttpServletRequest request){
        
        if( ((String)request.getSession().getAttribute("firstName")).trim().isEmpty() ){
            request.getSession().setAttribute ("errorMessage", "First name must not be empty.");
            return false;
        }
        if( ((String)request.getSession().getAttribute("lastName")).trim().isEmpty() ){
            request.getSession().setAttribute ("errorMessage", "Last name must not be empty.");
            return false;
        }
        if(! ((String)request.getSession().getAttribute("phoneFirst")).trim().matches("^[0-9]{3}$") ){
            request.getSession().setAttribute ("errorMessage", "Area code must be 3 digit number.");
            return false;
        }
        if(! ((String)request.getSession().getAttribute("phoneMiddle")).trim().matches("^[0-9]{3}$") ){
            request.getSession().setAttribute ("errorMessage", "The middle portion of phone number must be 3 digit number.");
            return false;
        }
        if(! ((String)request.getSession().getAttribute("phoneLast")).trim().matches("^[0-9]{4}$") ){
            request.getSession().setAttribute ("errorMessage", "The last portion of phone number must be 4 digit number.");
            return false;
        }
        if( ((String)request.getSession().getAttribute("shippingAddress")).trim().isEmpty() ){
            request.getSession().setAttribute ("errorMessage", "Shipping address must not be empty.");
            return false;
        }
        if( ! is_zip_valid(((String)request.getSession().getAttribute("zipCode")).trim()) ){//come back here
            request.getSession().setAttribute ("errorMessage", "Zip code must be valid.");
            return false;
        }
        if( ((String)request.getSession().getAttribute("city")).trim().isEmpty() ){
            request.getSession().setAttribute ("errorMessage", "Shipping city must not be empty.");
            return false;
        }
        if( ((String)request.getSession().getAttribute("state")).trim().isEmpty() ){
            request.getSession().setAttribute ("errorMessage", "Shipping state must not be empty.");
            return false;
        }
        if(! ((String)request.getSession().getAttribute("creditNumber")).trim().matches("^[0-9]{16}$") ){
            request.getSession().setAttribute ("errorMessage", "Card number must be 16 digit number.");
            return false;
        }
        if( ((String)request.getSession().getAttribute("creditName")).trim().isEmpty() ){
            request.getSession().setAttribute ("errorMessage", "Name on card must not be empty.");
            return false;
        }
        synchronized(mutex){
            int month = Integer.parseInt(((String)request.getSession().getAttribute("creditMonth")).trim());
            int year = Integer.parseInt(((String)request.getSession().getAttribute("creditYear")).trim());
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if( currentYear == year && currentMonth > month ){
                request.getSession().setAttribute ("errorMessage", "Credit card has expired! Please provide an alternative payment method.");
                return false;
            }
        }
        if(! ((String)request.getSession().getAttribute("creditCVC")).trim().matches("^[0-9]{3}$") ){
            request.getSession().setAttribute ("errorMessage", "CVC must be 3 digit number.");
            return false;
        }
        return true;
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
        request.getSession().setAttribute ("firstName", request.getParameter("firstname"));
        request.getSession().setAttribute ("lastName", request.getParameter("lastname"));
        request.getSession().setAttribute ("phoneFirst", request.getParameter("phonenumber_areacode"));
        request.getSession().setAttribute ("phoneMiddle", request.getParameter("phonenumber_middle"));
        request.getSession().setAttribute ("phoneLast", request.getParameter("phonenumber_last"));
        request.getSession().setAttribute ("shippingAddress", request.getParameter("shipping_address"));
        request.getSession().setAttribute ("zipCode", request.getParameter("shipping_zip"));
        request.getSession().setAttribute ("city", request.getParameter("shipping_city"));
        request.getSession().setAttribute ("state", request.getParameter("shipping_state"));
        request.getSession().setAttribute ("shippingMethod", request.getParameter("shipping_method"));
        request.getSession().setAttribute ("creditNumber", request.getParameter("credit_number"));
        request.getSession().setAttribute ("creditName", request.getParameter("credit_name_on_card"));
        request.getSession().setAttribute ("creditMonth", request.getParameter("credit_month"));
        request.getSession().setAttribute ("creditYear", request.getParameter("credit_year"));
        request.getSession().setAttribute ("creditCVC", request.getParameter("credit_cvc"));

        if ( is_form_valid(request) ) {
            try {
                synchronized(mutex){
                    preparedStmt = conn.prepareStatement("INSERT INTO orders (fname, lname, phone_number, shipping_address, shipping_method, credit_number, credit_name, credit_month, credit_year, credit_cvc, shipping_state, shipping_zip, shipping_city, pid, quantity, price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
                    preparedStmt.setString(1, (String)request.getSession().getAttribute("firstName"));
                    preparedStmt.setString(2, (String)request.getSession().getAttribute("lastName"));
                    preparedStmt.setString(3, (String)request.getSession().getAttribute("phoneFirst")+(String)request.getSession().getAttribute("phoneMiddle")+(String)request.getSession().getAttribute("phoneLast"));
                    preparedStmt.setString(4, (String)request.getSession().getAttribute("shippingAddress"));
                    preparedStmt.setString(5, (String)request.getSession().getAttribute("shippingMethod"));
                    preparedStmt.setString(6, (String)request.getSession().getAttribute("creditNumber"));
                    preparedStmt.setString(7, (String)request.getSession().getAttribute("creditName"));
                    preparedStmt.setString(8, (String)request.getSession().getAttribute("creditMonth"));
                    preparedStmt.setString(9, (String)request.getSession().getAttribute("creditYear"));
                    preparedStmt.setString(10, (String)request.getSession().getAttribute("creditCVC"));
                    preparedStmt.setString(11, (String)request.getSession().getAttribute("state"));
                    preparedStmt.setString(12, (String)request.getSession().getAttribute("zipCode"));
                    preparedStmt.setString(13, (String)request.getSession().getAttribute("city"));
                    String collectivePid="", collectiveQuantity = "";
                    float db_price = 0;
                    for(String key: ((shoppingCart) request.getSession().getAttribute("shoppingCart")).getCartItems().keySet()){
                        if ( key != null )
                        {
                            stmt = conn.createStatement();
                            rs = stmt.executeQuery("select price from product where pid='"+key+"'");
                            if ( ! rs.next() ){
                                // CRITICAL ERROR
                                request.getRequestDispatcher("error.html").forward(request, response);
                            } 
                            collectivePid += key;
                            collectivePid += ";";
                            collectiveQuantity += ((shoppingCart) request.getSession().getAttribute("shoppingCart")).getCartItems().get(key).toString();
                            collectiveQuantity += ";";
                            db_price += rs.getFloat("price")*(int)((shoppingCart) request.getSession().getAttribute("shoppingCart")).getCartItems().get(key);
                        }
                    }
                    preparedStmt.setString(14, collectivePid);
                    preparedStmt.setString(15, collectiveQuantity);
                    preparedStmt.setFloat(16, db_price);
                    PrintWriter temp = response.getWriter();
                    preparedStmt.executeUpdate();
                    rs = preparedStmt.getGeneratedKeys();
                    if (rs.next()){
                        int id=rs.getInt(1);
                        request.getSession().setAttribute("firstName","");
                        request.getSession().setAttribute("lastName","");
                        request.getSession().setAttribute("phoneFirst","");
                        request.getSession().setAttribute("phoneMiddle","");
                        request.getSession().setAttribute("phoneLast","");
                        request.getSession().setAttribute("shippingAddress","");
                        request.getSession().setAttribute("zipCode","");
                        request.getSession().setAttribute("city","");
                        request.getSession().setAttribute("state","");
                        request.getSession().setAttribute("creditNumber","");
                        request.getSession().setAttribute("creditName","");
                        request.getSession().setAttribute("creditCVC","");
                        
                        ((shoppingCart) request.getSession().getAttribute("shoppingCart")).getCartItems().clear();
                        request.getRequestDispatcher("confirmation.jsp?id="+Integer.toString(id)).forward(request, response);
                    } else
                    {
                        //error
                        try{
                            PrintWriter out = response.getWriter();
                            out.println("<!DOCTYPE html><script type='text/javascript'>alert('An error occured while submitting your order. Please try again.');</script>");
                            processRequest(request,response);
                        }catch(SQLException e){
                            e.printStackTrace();
                        }   
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        } else
        {
            try{
                PrintWriter out = response.getWriter();
                out.println("<!DOCTYPE html><script type='text/javascript'>alert('"+(String)request.getSession().getAttribute("errorMessage")+"');</script>");
                processRequest(request,response);
            }catch(SQLException e){
                e.printStackTrace();
            }   
        }
        
        
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        
        //HttpSession s = request.getSession (true); //DO NOT USE THIS. NOT THREAD SAFE.
        if ( (String)request.getSession().getAttribute("firstName") == null ) {
            request.getSession().setAttribute("firstName","");
        }
        if ( (String)request.getSession().getAttribute("lastName") == null ) {
            request.getSession().setAttribute("lastName","");
        }
        if ( (String)request.getSession().getAttribute("phoneFirst") == null ) {
            request.getSession().setAttribute("phoneFirst","");
        }
        if ( (String)request.getSession().getAttribute("phoneMiddle") == null ) {
            request.getSession().setAttribute("phoneMiddle","");
        }
        if ( (String)request.getSession().getAttribute("phoneLast") == null ) {
            request.getSession().setAttribute("phoneLast","");
        }
        if ( (String)request.getSession().getAttribute("shippingAddress") == null ) {
            request.getSession().setAttribute("shippingAddress","");
        }
        if ( (String)request.getSession().getAttribute("zipCode") == null ) {
            request.getSession().setAttribute("zipCode","");
        }
        if ( (String)request.getSession().getAttribute("city") == null ) {
            request.getSession().setAttribute("city","");
        }
        if ( (String)request.getSession().getAttribute("state") == null ) {
            request.getSession().setAttribute("state","");
        }
        if ( (String)request.getSession().getAttribute("creditNumber") == null ) {
            request.getSession().setAttribute("creditNumber","");
        }
        if ( (String)request.getSession().getAttribute("creditName") == null ) {
            request.getSession().setAttribute("creditName","");
        }
        if ( (String)request.getSession().getAttribute("creditCVC") == null ) {
            request.getSession().setAttribute("creditCVC","");
        }
        
        try {
            PrintWriter out = response.getWriter();
            /* Including .css file and setting the title */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\" />");
            out.println("<title> Checkout </title>");  
            out.println("</head>");
            out.println("<body>");
            
            // include our menu bar
            request.getRequestDispatcher("WEB-INF/header.html").include(request, response);
            
            // content
            
            // just add the cart
            //shoppingCart cart;
            
            
            if(request.getParameter("remove") != null){
                ((shoppingCart) request.getSession().getAttribute("shoppingCart")).deleteproduct(request.getParameter("remove"));
                // if cart is empty, alert error message
                if ( ((shoppingCart) request.getSession().getAttribute("shoppingCart")).getCartItems().isEmpty() )
                {
                    request.getRequestDispatcher("intermediate.html").forward(request, response);
                }
            }
            synchronized(mutex){
                
                double totoal_price = 0;
                
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
                                out.println("<img src='picture/"+types+"/"+name+"/"+name+"_main.jpg' alt='"+name+ "/" + "width='150px' height='150px'" +"'/>");                                               
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
                    
                    out.println("<script src=\"jQuery.js\"></script>");
                    out.println("<script src=\"ajax.js\"></script>");
                    out.println("<b> Total Price:  $</b>");
                    out.println("<b id='new_price' style='color:#DC143C;font-size:20px;'>");
                    out.println(Math.round(totoal_price*100.0)/100.0 +"</b>"); //change the price here
                    out.println("<input id='total_price' type='hidden' name='total_price' value='" +Float.toString((float)(Math.round(totoal_price*100.0)/100.0)) +"'>");
                    out.println("</input>");
                    
                    

                out.println("</div>");
                
            }
            
            // end of cart

            out.println("<br/><form name=\"myForm\" method=\"post\" action=\"\">\n" +
"		<table align = \"center\" >\n");
            out.println(
"			<tr>\n" +
"				<td>\n" +
"					First Name: \n" +
"				</td>\n" +
"				<td>\n" +
"					<input type=\"text\" name=\"firstname\" value=\""+(String)request.getSession().getAttribute("firstName")+"\"/>\n" +
"				</td>\n" +
"			</tr>\n" +
"			<tr>\n" +
"				<td>\n" +
"					Last Name: \n" +
"				</td>\n" +
"				<td>\n" +
"					<input type=\"text\" name=\"lastname\" value=\""+(String)request.getSession().getAttribute("lastName")+"\" />\n" +
"				</td>\n" +
"			</tr>\n" +
"			<tr>\n" +
"				<td>\n" +
"					Phone number (Format: 123 456 7890): \n" +
"				</td>\n" +
"				<td>\n" +
"					<input type=\"text\" name=\"phonenumber_areacode\" size=\"3\" value=\""+(String)request.getSession().getAttribute("phoneFirst")+"\"/>\n" +
"					<input type=\"text\" name=\"phonenumber_middle\" size=\"3\" value=\""+(String)request.getSession().getAttribute("phoneMiddle")+"\"/>\n" +
"					<input type=\"text\" name=\"phonenumber_last\" size=\"4\" value=\""+(String)request.getSession().getAttribute("phoneLast")+"\"/>\n" +
"				</td>\n" +
"			</tr>\n" +
"			<tr>\n" +
"				<td>\n" +
"					Shipping Address: \n" +
"				</td>\n" +
"				<td>\n" +
"					<input type=\"text\" name=\"shipping_address\" value=\""+(String)request.getSession().getAttribute("shippingAddress")+"\"/>\n" +
"				</td>\n" +
"			</tr>\n" +
"			<tr>\n" +
"				<td>\n" +
"					Zip code:\n" +
"				</td>\n" +
"				<td>\n" +
"					<input id=\"input_zip\" type=\"text\" name=\"shipping_zip\" size=\"5\" value=\""+(String)request.getSession().getAttribute("zipCode")+"\"/>\n" +
"				</td>\n" +
"			</tr>\n" +
"		\n" +
" 			<tr>\n" +
"				<td>\n" +
"					City:\n" +
"				</td>\n" +
"				<td>\n" +
"                                       <script src=\"jQuery.js\"></script>\n" +
"                                       <script src=\"zip.js\"></script>\n" +
"					<input id=\"zip_city\" type=\"text\" name=\"shipping_city\" value=\""+(String)request.getSession().getAttribute("city")+"\" />\n" +
"				</td>\n" +
"			</tr>\n" +
"\n" +
"			<tr>\n" +
"				<td>\n" +
"					State:\n" +
"				</td>\n" +
"				<td>\n" +
"					<input id=\"zip_state\" type=\"text\" name=\"shipping_state\" value=\""+(String)request.getSession().getAttribute("state")+"\" />\n" +
"				</td>\n" +
"			</tr>"+
"			<tr>\n" +
"				<td>\n" +
"					Shipping Method: \n" +
"				</td>\n" +
"				<td>\n" +
"					<select name=\"shipping_method\">\n" +
"					<option value=\"ground\">Ground</option>\n" +
"					<option value=\"standard\">Standard</option>\n" +
"					</select>\n" +
"				</td>\n" +
"					\n" +
"			</tr>\n" +
"			<tr>\n" +
"				<td>\n" +
"					Credit Card Number (Enter 16 digit number only): \n" +
"				</td>\n" +
"				<td>\n" +
"					<input type=\"text\" name=\"credit_number\" value=\""+(String)request.getSession().getAttribute("creditNumber")+"\"/>\n" +
"				</td>\n" +
"			</tr>\n" +
"			<tr>\n" +
"				<td>\n" +
"					Name on Card: \n" +
"				</td>\n" +
"				<td>\n" +
"					<input type=\"text\" name=\"credit_name_on_card\" value=\""+(String)request.getSession().getAttribute("creditName")+"\" />\n" +
"				</td>\n" +
"			</tr>\n" +
"			<tr>\n" +
"				<td>\n" +
"					Expiration Date: \n" +
"				</td>\n" +
"				<td>\n" +
"					<select name=\"credit_month\">\n" +
"					<option value=\"1\">1</option>\n" +
"					<option value=\"2\">2</option>\n" +
"					<option value=\"3\">3</option>\n" +
"					<option value=\"4\">4</option>\n" +
"					<option value=\"5\">5</option>\n" +
"					<option value=\"6\" selected>6</option>\n" +
"					<option value=\"7\">7</option>\n" +
"					<option value=\"8\">8</option>\n" +
"					<option value=\"9\">9</option>\n" +
"					<option value=\"10\">10</option>\n" +
"					<option value=\"11\">11</option>\n" +
"					<option value=\"12\">12</option>\n" +
"					</select>\n" +
"					<select name=\"credit_year\">\n" +
"					<option value=\"2016\">2016</option>\n" +
"					<option value=\"2017\">2017</option>\n" +
"					<option value=\"2018\">2018</option>\n" +
"					<option value=\"2019\">2019</option>\n" +
"					<option value=\"2020\">2020</option>\n" +
"					<option value=\"2021\">2021</option>\n" +
"					<option value=\"2022\">2022</option>\n" +
"					<option value=\"2023\">2023</option>\n" +
"					<option value=\"2024\">2024</option>\n" +
"					<option value=\"2025\">2025</option>\n" +
"					<option value=\"2026\">2026</option>\n" +
"					</select>\n" +
"				</td>\n" +
"			</tr>\n" +
"			<tr>\n" +
"				<td>\n" +
"					CVC (Enter 3 digit number only): \n" +
"				</td>\n" +
"				<td>\n" +
"					<input type=\"text\" name=\"credit_cvc\" value=\""+(String)request.getSession().getAttribute("creditCVC")+"\" />\n" +
"				</td>\n" +
"			</tr>\n" +
"			<tr>\n" +
"				<td>\n" +
"				</td>\n" +
"				<td>\n" +
"					<input class=\"form-button\" type=\"submit\" value=\"Submit Order\" />\n" +
"				</td>\n" +
"			</tr>\n" +
"		</table>\n" +
"	</form><br/><br/><br/>");

            
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
            synchronized(mutex){
                if(stmt != null){
                    stmt.close();
                }
                if(preparedStmt != null){
                    preparedStmt.close();
                }
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
