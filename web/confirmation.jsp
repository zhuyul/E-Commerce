<%@page import="java.sql.*"%>
<%@page import="javax.sql.*"%>
<%@page import="cs137.DbAccess"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css" href="style.css" />
<title> Confirmation </title>          
</head>
<body>
<jsp:include page="WEB-INF/header.html" /> 
<%! String id;
    Connection conn = null;
    PreparedStatement preparedStmt = null;
    Statement  stmt = null;
    ResultSet   rs = null;
    ResultSet   rs1 = null;
    String mutex="";
    String[] pids, quantities;
    public void jspInit() {
        try{
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DbAccess.Dblocation,DbAccess.AccountID,DbAccess.Password);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
        }
    }
    public void jspDestroy() {
        try{
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
    }
%>
<%
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

id = request.getParameter("id");//check for error (NumberUtils.isNumber)
if (id != null && id.matches("^[1-9]{1}[0-9]*$"))
{
    try{
        synchronized(mutex){

            preparedStmt = conn.prepareStatement("SELECT pid, quantity, fname, lname, phone_number, shipping_address, shipping_method, shipping_state, shipping_city, shipping_zip, credit_number, credit_name, credit_month, credit_year, credit_cvc, price FROM orders WHERE id=?");
            preparedStmt.setInt(1, Integer.parseInt(id));//check for error
            rs = preparedStmt.executeQuery();
            if (! rs.next()){
                // PRINT ERROR MESSAGE
%>
<br/>
<br/>
<br/>
<center> 
Warning! Valid order id is not provided for confirmation page.
</center>
<%
            } else
            {
                // display the following table
%>
<br/>
<center> 
Thank you for submitting order! Your order is recorded in our database and will be processed soon.<br>
For your information, this confirmation page includes details about your order.
</center>
<br/>

<%
                pids = rs.getString("pid").split(";");
                quantities = rs.getString("quantity").split(";");
                for (int i = 0; i < pids.length; ++i){
                    stmt = conn.createStatement();
                    rs1 = stmt.executeQuery("select name, price, types, long_title from product where pid='"+pids[i]+"'");
                    if (! rs1.next()){
%>
<center> 
Error occurred while getting specific product information.
</center>
<%
                    } else {
                
%>
<div style='height:200px;padding-left: 10%;padding-right: 15%; padding-top: 20px'>
<div style="background-color:#FFFFFF; width:200px; height:200px; float:left; text-align:center">
<img src='picture/<%=rs1.getString("types")%>/<%=rs1.getString("name")%>/<%=rs1.getString("name")%>_main.jpg' alt='<%=rs1.getString("name")%>' width='150px' height='150px''/>
</div>
<div>
<font size = "3"><%=rs1.getString("long_title")%></font><div>
<b>Price </b>
<font size="5" style="position: absolute; right: 20%;"><%=rs1.getFloat("price")%></font>
</div>
<div>
<b> Quantity </b>
<font size="5" style=" right: 20%; position: absolute;"><%=quantities[i]%></font>
</div>
</div>
</div>    
<%  
                    }
                }
%>
<table align = "center" >
        <tr>
                <td>
                        Total price:
                </td>
                <td>
                        $ <%=rs.getFloat("price")%>
                </td>
        </tr>
        <tr>
                <td>
                        First Name:
                </td>
                <td>
                        <%=rs.getString("fname")%>
                </td>
        </tr>
        <tr>
                <td>
                        Last Name:
                </td>
                <td>
                    <%=rs.getString("lname")%>
                </td>
        </tr>
        <tr>
                <td>
                        Phone number:
                </td>
                <td>
                    <%=rs.getString("phone_number")%>
                </td>
        </tr>
        <tr>
                <td>
                        Shipping Address:
                </td>
                <td>
                    <%=rs.getString("shipping_address")%>
                </td>
        </tr>
        <tr>
                <td>
                        Zip code:
                </td>
                <td>
                    <%=rs.getString("shipping_zip")%>
                </td>
        </tr>

        <tr>
                <td>
                        City:
                </td>
                <td>
                    <%=rs.getString("shipping_city")%>
                </td>
        </tr>

        <tr>
                <td>
                        State:
                </td>
                <td>
                    <%=rs.getString("shipping_state")%>
                </td>
        </tr>

        <tr>
                <td>
                        Shipping Method:
                </td>
                <td>
                    <%=rs.getString("shipping_method")%>
                </td>

        </tr>
        <tr>
                <td>
                        Credit Card Number:
                </td>
                <td>
                    <%=rs.getString("credit_number")%>
                </td>
        </tr>
        <tr>
                <td>
                        Name on Card:
                </td>
                <td>
                    <%=rs.getString("credit_name")%>
                </td>
        </tr>
        <tr>
                <td>
                        Expiration Date:
                </td>
                <td>
                    <%=rs.getString("credit_month")%>/<%=rs.getString("credit_year")%>
                </td>
        </tr>
        <tr>
                <td>
                        CVC:
                </td>
                <td>
                    <%=rs.getString("credit_cvc")%>
                </td>
        </tr>
        <tr>
                <td>
                </td>
                <td>
                </td>
        </tr>
</table>
<%
            }
        }
    }catch(Exception x){
%>
<br/>
<br/>
<br/>
<center> 
Warning! Error accessing the database.
</center>
<%
    }
} else {
%>
<br/>
<br/>
<br/>
<center> 
Warning! Valid order id is not provided for confirmation page.
</center>
<%
}

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
%>
<br/>
<br/>
<br/>
<jsp:include page="WEB-INF/footer.html" /> 
