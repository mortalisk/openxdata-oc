<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ taglib prefix='fmt' uri='http://java.sun.com/jsp/jstl/fmt' %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page isELIgnored="false" %>
<c:choose>
  <c:when test="${!empty param.locale}">
    <!-- 1: get locale from the request parameter -->
    <c:set var="locale" scope="session" value="${param.locale}" />
  </c:when>
  <c:otherwise>
    <c:if test="${empty locale}">
       <c:choose>
         <c:when test="${empty cookie.locale.value}">
           <!-- 2: get locale from the browser header: ${pageContext.response.locale} -->
           <c:set var="locale" scope="session" value="${pageContext.response.locale.language}" />
         </c:when>
         <c:otherwise>
           <!-- 3: get the locale from the cookie -->
           <c:set var="locale" scope="session" value="${cookie.locale.value}"/>
         </c:otherwise>
       </c:choose>
    </c:if>
  </c:otherwise>
</c:choose>
<!-- setting session variable and cookie locale=${locale} -->
<fmt:setLocale scope="session" value="${locale}" />
<%
Cookie localeCookie = new Cookie("locale", (String)session.getAttribute("locale"));
localeCookie.setMaxAge(0x7ffffff);
response.addCookie(localeCookie);
%>
<%-- <c:set var="cookie.locale.value" value="${locale}" /> --%>
