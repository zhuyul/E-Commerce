<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html>

<head>
	<link rel="stylesheet" type="text/css" href="style.css" />
	<script src="contact.js"></script>
	<title> Ecommerce company group 20 </title>
</head>


 
<body>


	<jsp:include page="WEB-INF/header.html" />
	

	<div>
		<form name="myForm" method="post" target="_blank">
			<div>
				<p style="margin-left: 5%; padding-top: 2%;font-size:16px">Subject:</p>
				<textarea id="subject" rows="1" cols="30" placeholder="Enter subject here.." class="contact_box"> </textarea>
			</div>

			<div>
				<p style="margin-left: 5%; font-size:16px">Please enter your content:</p>
				<textarea id="inputs" rows="15" cols="70" placeholder="Enter content here.." class="contact_box"> </textarea>
			</div>
			
			<div style="margin-left: 5%; margin-bottom: 5%; padding-top: 2%">
				<input onclick="submitContent()" class="form-button" type="button" value="Send" />
				<input onclick="resetContent()" class="form-button" type="button" value="Reset" />			
			</div>

		</form>
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