<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!doctype html>
<html>

<head>
	<link rel="stylesheet" type="text/css" href="style.css" />
	<title> Ecommerce Homepage company info </title>
</head>

<body>
	<jsp:include page="WEB-INF/header.html" />


	<div >
		<table class="company_info">
			<tr>
				<td class="left">
					<p>Name of Company</p>
				</td>
				<td>
					<p> Group 20</p>
				</td>
			</tr>
			<tr>
				<td class="left">
					<p>Company objective</p>
				</td>
				<td>
					<p>E-commerce for Electronics</p>
				</td>
			</tr>

			<tr>
				<td class="left">
					<p>Head Office</p>
				</td>
				<td>
					<p>Univiersity of California, Irvine.</p>
				</td>
			</tr>
			<tr>
				<td class="left">
					<p>Team Members</p>
				</td>
				<td>
					<p>Asher Niu, Jungkyu Park</br>
					Lewis Liu, Zhuyu Li
					</p>
				</td>
			</tr>

		</table>

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