<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.htmlpage.web.HtmlPageJspBean"%>

${ htmlPageJspBean.init( pageContext.request, HtmlPageJspBean.RIGHT_MANAGE_HTMLPAGE ) }
${ htmlPageJspBean.getModifyHtmlPage ( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>