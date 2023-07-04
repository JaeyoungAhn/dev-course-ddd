<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%--bootstrap can be added--%>
<!DOCTYPE html>
<html>
<head>
    <title>Sample JSP Page</title>
</head>
<body>
<h1>Hello, JSP!</h1>
<%--<p>The time on the server is <%= request.getAttribute("serverTime") %></p>--%>
<p>The time on the server is ${serverTime}</p>
<c:forEach var="customers" items="${customers}">${i}</c:forEach>
<%--<%--%>
<%--    --%>
<%--    for(int i=0; i<; i++) --%>
<%--%>--%>
<%-- <%--%>
<%--%>--%>
</body>
</html>