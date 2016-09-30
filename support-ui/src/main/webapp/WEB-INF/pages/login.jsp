<%@ page language="java" contentType="text/html; charset=utf8"
         pageEncoding="utf8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="date" class="java.util.Date"/>

<!DOCTYPE>
<html>
<head>
    <title>PEPPOL AP - Sign In</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/oc-style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css"/>
    <link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/favicon.ico"/>
</head>
<body>
<div id="wrap">
    <div id="header">
        <div class="wrap">
            <a href="${pageContext.request.contextPath}" id="logo"><img src="../../images/logo.png"></a>
            <span id="header_title">PEPPOL AP</span>
        </div>
    </div>
    <div id="main">
        <div class="container">

            <form class="form-signin" name="log_in" action="${pageContext.request.contextPath}/login" method="POST">
                <h2 class="form-signin-heading">Please sign in</h2>
                <input type="text" name="username" class="form-control" placeholder="Username" autofocus>
                <input type="password" name="password" class="form-control" placeholder="Password">
                <%--<label class="checkbox">
                    <input type="checkbox" value="remember-me"> Remember me
                </label>--%>
                <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
            </form>
            <c:if test="${error == true}">
                <div class="alert alert-danger error-box">
                        <%--<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>--%>
                    <strong>Error:</strong> ${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
                </div>
            </c:if>

        </div>
    </div>
</div>

<footer id="footer">
    <div class="wrap">
        <span class="right"><a href="http://www.itella.com" target="_blank">Part of Itella Group</a></span>
        <section class="copy">
            <span>© OpusCapita <fmt:formatDate value="${date}" pattern="yyyy"/></span>
        </section>
    </div>
</footer>
</body>
</html>