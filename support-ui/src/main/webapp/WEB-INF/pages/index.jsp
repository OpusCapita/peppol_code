<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<jsp:useBean id="date" class="java.util.Date"/>
<!DOCTYPE html>
<html class="no-js">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Peppol AP status</title>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width">
    <!-- Place favicon.ico and apple-touch-icon.png in the root directory -->

    <!-- build:css(.tmp) styles/main.css -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/js/lib/ng-table-master/ng-table.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/oc-style.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">

    <!-- endbuild -->
</head>
<body ng-app="peppolApp">
<!--[if lt IE 7]>
<p class="browsehappy">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade
    your browser</a> to improve your experience.</p>
<![endif]-->
<div id="wrap">
    <div id="header">
        <div class="wrap">
            <a href="/" id="logo"><img src="${pageContext.request.contextPath}/images/logo.png"></a>
        </div>
        <span id="header_title">PEPPOL AP</span>

        <div id="header_menu_bar">
            <span class="glyphicon glyphicon-user"></span> <i>${user.firstName} ${user.lastName}</i> <a href="/signout">Sign
            Out</a>
        </div>
    </div>
    <div id="main">
        <div class="content">
            <div class="row">
                <div class="col-lg-2 menu" ng-controller="sideMenu">
                    <ul nav-menu="active" class="nav <%--nav-pills nav-stacked--%>" ng-click="">
                        <li><a href="/customers"><span class="badge pull-right">{{customerCount}}</span>Customers</a>
                        <li><a href="/access_point"><span class="badge pull-right">{{accessPointCount}}</span>Access
                            Points</a>
                        </li>
                        <li>
                            <a href="#" class="tree-toggle"><b>Outbound messages</b></a>
                            <ul class="nav tree">
                                <li><a href="/outbound"><span class="badge pull-right">{{outAllCount}}</span>All
                                    messages</a></li>
                                <li><a href="/outbound/processing"><span
                                        class="badge pull-right">{{outProcessingCount}}</span>Processing messages</a>
                                </li>
                                <li><a href="/outbound/reprocessed"><span class="badge pull-right">{{outReprocessedCount}}</span>Reprocessed
                                    messages</a></li>
                                <li><a href="/outbound/failed"><span class="badge pull-right">{{outFailedCount}}</span>Failed
                                    messages</a></li>
                                <li><a href="/outbound/invalid"><span
                                        class="badge pull-right">{{outInvalidCount}}</span>Invalid messages</a></li>
                            </ul>
                        </li>
                        <li>
                            <a href="#" class="tree-toggle"><b>Inbound messages</b></a>
                            <ul class="nav tree">
                                <li><a href="/inbound"><span class="badge pull-right">{{inAllCount}}</span>All messages</a>
                                </li>
                                <li><a href="/inbound/invalid"><span class="badge pull-right">{{inInvalidCount}}</span>Invalid
                                    messages</a></li>
                            </ul>
                        </li>
                        <li><a href="/status">PEPPOL status</a></li>
                    </ul>
                </div>
                <div class="col-lg-10" ng-view=""></div>
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
</div>


<script src="${pageContext.request.contextPath}/js/lib/jquery-1.11.0.min.js"></script>
<script src="${pageContext.request.contextPath}/js/lib/angular-1.2.11/angular.js"></script>
<%--
<script src="${pageContext.request.contextPath}/js/lib/bootstrap.min.js"></script>
--%>
<script src="${pageContext.request.contextPath}/js/lib/angular-1.2.11/angular-resource.js"></script>
<script src="${pageContext.request.contextPath}/js/lib/angular-1.2.11/angular-cookies.js"></script>
<script src="${pageContext.request.contextPath}/js/lib/angular-1.2.11/angular-route.js"></script>
<script src="${pageContext.request.contextPath}/js/lib/angular-1.2.11/angular-animate.js"></script>
<script src="${pageContext.request.contextPath}/js/lib/angular-1.2.11/angular-sanitize.js"></script>
<script src="${pageContext.request.contextPath}/js/lib/ng-table-master/ng-table.src.js"></script>
<script src="${pageContext.request.contextPath}/js/lib/ui-bootstrap-tpls-0.11.0.js"></script>
<%--<script src="${pageContext.request.contextPath}/js/lib/ui-bootstrap-0.11.0.min.js"></script>--%>


<!-- build:js({.tmp,app}) scripts/scripts.js -->
<script src="${pageContext.request.contextPath}/js/app.js"></script>
<script src="${pageContext.request.contextPath}/js/services/services.js"></script>
<script src="${pageContext.request.contextPath}/js/controllers/controllers.js"></script>
<script src="${pageContext.request.contextPath}/js/directives.js"></script>
<!-- endbuild -->
</body>
</html>