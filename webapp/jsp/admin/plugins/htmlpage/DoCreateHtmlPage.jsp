<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="htmlpage" scope="session" class="fr.paris.lutece.plugins.htmlpage.web.HtmlPageJspBean" />

<%
    htmlpage.init( request, htmlpage.RIGHT_MANAGE_HTMLPAGE );
    response.sendRedirect( htmlpage.doCreateHtmlPage( request ) );
%>
