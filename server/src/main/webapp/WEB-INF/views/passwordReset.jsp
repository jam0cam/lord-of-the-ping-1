<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="pageTitle" value="Lord of the Ping - Password Reset" scope="request"/>

<jsp:include page="./header.jsp"/>

<div class="container">
    <form:form class="form-signin" action="/password/reset" method="post" commandName="command">
        <h2>Password Reset</h2>
        <form:errors path="*" element="div" cssClass="alert alert-error"/>
        <form:input path="email" type="text" class="form-control" value="${command.email}" readonly="true"/>
        <form:input path="newPassword"  type="password" class="form-control" placeholder="Password" required=""/>
        <form:hidden path="passwordHash" />
        <button class="btn btn-lg btn-primary btn-block" type="submit">Submit</button>
    </form:form>
</div> <!-- /container -->


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="js/bootstrap.min.js"></script>
</body>
</html>
