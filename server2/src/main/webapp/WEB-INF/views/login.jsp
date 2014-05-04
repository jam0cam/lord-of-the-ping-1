<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="pageTitle" value="Lord of the Ping - Sign In" scope="request"/>

<jsp:include page="./header.jsp"/>

<div class="container">
    <form:form class="form-signin" action="/login" method="post" commandName="command">
        <h2>Sign In</h2>

        <c:if test="${command.error != null}">
            <div class="alert alert-error">
                    ${command.error}
            </div>
        </c:if>

        <c:if test="${param.logout != null}">
            <div class="alert alert-success">
                You have been logged out.
            </div>
        </c:if>

        <form:input path="email" type="text" class="form-control" placeholder="Email Address" required="" autofocus=""/>
        <form:input path="password"  type="password" class="form-control" placeholder="Password" required=""/>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
    </form:form>
    <div class="secondary-block">
        <p class="text-center secondary-action"><span>or</span></p>
        <a href="/register" class="btn btn-lg btn-default btn-block">Register</a>
    </div>
</div> <!-- /container -->


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="js/bootstrap.min.js"></script>
</body>
</html>
