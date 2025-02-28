<%@ page errorPage="../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.htmlpage.web.HtmlPageJspBean"%>

${ htmlPageJspBean.init( pageContext.request, HtmlPageJspBean.RIGHT_MANAGE_HTMLPAGE ) }
${ pageContext.response.sendRedirect( htmlPageJspBean.doRemoveHtmlPage( pageContext.request ) ) }
