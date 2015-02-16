<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="pageTitle" value="Lord of the Ping - Table Tennis Tracker" scope="request"/>
<c:set var="pageType" value="inbox" scope="request"/>

<jsp:include page="./header.jsp"/>
<form:form commandName="command">

    <div class="container">
        <h2>Inbox</h2>
        <div class="table-responsive">
            <table class="table table-striped">
                <tbody>


                <c:if test="${empty command.pendingMatches}">
                    <div class="alert alert-info">There are no messages.</div>
                </c:if>

                <c:forEach var="item" items="${command.pendingMatches}" varStatus="loop">
                    <tr>
                        <td><img alt="180x180" height="180" width="180" class="img-circle" src="${item.playerOne.avatarUrl}"></td>
                        <td><a href="/profile/${item.playerOne.id}">${item.playerOne.name}</a>
                            <p class="small">
                                <c:choose>
                                    <c:when test="${item.status=='W'}">
                                        Winning
                                    </c:when>
                                    <c:otherwise>
                                        Losing
                                    </c:otherwise>
                                </c:choose>
                                match confirmation (${item.p2Score}-${item.p1Score})
                            </p>
                        </td>
                        <td>
                            <p>
                                <button type="button" onclick="window.location.href='/inbox/confirmMatch/${item.id}'" class="btn btn-primary btn-sm">Confirm</button>
                                <button type="button" onclick="window.location.href='/inbox/declineMatch/${item.id}'" class="btn btn-default btn-sm">Decline</button>
                            </p>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

    </div> <!-- /container -->

</form:form>

<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
<script src="js/bootstrap.min.js"></script>
</body>
</html>
