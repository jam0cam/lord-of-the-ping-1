<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="pageTitle" value="Lord of the Ping - Leaderboard" scope="request"/>
<c:set var="pageType" value="home" scope="request"/>

<jsp:include page="./header.jsp"/>
    <div class="container full">
		<h2>Leaderboard</h2>
		<div class="table-responsive">
			<table class="table table-striped">
				<thead>
					<tr>
						<th></th>
                        <th style="width: 80px">Rating</th>
						<th>Name</th>
						<th>W</th>
						<th>L</th>
					</tr>
				</thead>
				<tbody>
                <c:forEach var="item" items="${command}" varStatus="loop">
                    <tr>
                        <td>${loop.index+1}</td>
                        <td>${item.player.ranking}</td>
                        <td><a href="/profile/${item.player.id}">${item.player.name}</a></td>
                        <td>${item.matchWins}</td>
                        <td>${item.matchLosses}</td>
                    </tr>
                </c:forEach>
				</tbody>
			</table>
		</div>


        <div class="bottom-right">
            <p>Download the Android App <a href="https://www.dropbox.com/s/vcrphd1rbqu4rkq/lotp-release.apk">Here!</a></p>
        </div>
    </div> <!-- /container -->



<jsp:include page="./footer.jsp"/>
