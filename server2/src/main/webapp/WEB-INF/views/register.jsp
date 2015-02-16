<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="pageTitle" value="Lord of the Ping - Register" scope="request"/>

<jsp:include page="./header.jsp"/>
<div class="container">
    <form:form class="form-signin"  action="/register"  method="post" commandName="command" >
        <form:errors path="*" element="div" cssClass="alert alert-error"/>
        <h2>Register</h2>
        <form:input path="name" type="text" class="form-control" placeholder="First & Last Name" required="" autofocus=""/>
        <form:input path="email" type="text" class="form-control" placeholder="Email Address" required=""/>
        <form:input path="password"  type="password" class="form-control" placeholder="Password" required=""/>
        <button class="btn btn-lg btn-primary btn-block" type="submit">Submit</button>
    </form:form>
    <div class="secondary-block">
        <p class="text-center secondary-action"><span>or</span></p>
        <a href="/signin" class="btn btn-lg btn-default btn-block">Sign In</a>
    </div>
</div> <!-- /container -->


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="js/bootstrap.min.js"></script>
</body>
</html>
