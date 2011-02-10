<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="htmlpage" scope="session" class="fr.paris.lutece.plugins.htmlpage.web.HtmlPageJspBean" />

<% htmlpage.init( request, htmlpage.RIGHT_MANAGE_HTMLPAGE ); %>
<%= htmlpage.getModifyHtmlPage ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>