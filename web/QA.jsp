<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
	<link rel="stylesheet" type="text/css" href="style.css" />
	<title> Ecommerce Homepage Q&amp;A </title>
</head>
 
<body>

	<jsp:include page="WEB-INF/header.html" />

	<div class = "QA">
		<h3> Question: How to checkout? </h3>
		<p>&#149; Please remember the product indentify and click the checkout icon on the navigation bar.</p>

		<h3> Question: How to track my package? </h3>
		<p>&#149; To track your package, please reply to the confirmation email with "Track" as your subjects.</p>
		<p>&#149; The server will automatically reply with more information.</p>

		<h3> Question: How to find a missing package that shows as delievered? </h3>
		<p>&#149; Please verfiy your shipping address. Check notices for attempted delievery. </p>
		<p>&#149; If you cannot locate your package within 36 hours, please reorder.</p>

		<h3> Question: How to cancel my order? </h3>
		<p>&#149; To cancel your order, please use contact us to send us an email.</p>

		<h3> Question: How to get refunds if I cancel my order? </h3>
		<p>&#149; NO REFUNDS.</p>


	</div>
        <%! 
            String mutex="";
        %>
        <%
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


	<jsp:include page="WEB-INF/footer.html" /> 
