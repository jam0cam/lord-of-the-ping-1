<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="pageTitle" value="Lord of the Ping - Leaderboard" scope="request"/>
<c:set var="pageType" value="match" scope="request"/>

<jsp:include page="./header.jsp"/>
        <div class="text-center">
            <br>
            <p>Your match has been sent to ${command.name} for confirmation!</p>
            <br>
            <a href="/">Home</a> | <a href="/match">New Match</a>
        </div>
    </div> <!-- /container -->

<jsp:include page="./footer.jsp"/>
