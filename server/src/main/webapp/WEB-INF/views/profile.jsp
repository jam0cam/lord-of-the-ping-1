<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="pageTitle" value="Lord of the Ping - Profile" scope="request"/>
<form:form commandName="command">
    <c:set var="pageType" value="profile" scope="request"/>
    <c:set var="ownProfile" value="${command.ownProfile}" scope="request"/>
</form:form>


<jsp:include page="./header.jsp"/>
<form:form commandName="command">

    <script src="/js/Chart.min.js"></script>
    <div class="container">
        <h2>Profile</h2>
        <div class="col-md-3">
            <div class="panel panel-default">
                <div class="panel-body">

                    <c:choose>
                        <c:when test="${fn:containsIgnoreCase(command.player.avatarUrl, 'google')}">
                            <img alt="180x180" height="180" width="180" class="img-circle" src="${command.player.avatarUrl}?sz=180">
                        </c:when>
                        <c:otherwise>
                            <img alt="180x180" height="180" width="180" class="img-circle" src="${command.player.avatarUrl}?size=180">
                        </c:otherwise>
                    </c:choose>

                    <h3 class="text-center">${command.player.name}</h3>
                    <h4>(${command.player.ranking})</h4>
                </div>
                <div class="panel-heading">
                    <h3 class="panel-title">Matches <span class="badge pull-right">${command.stats.totalMatches}</span></h3>
                </div>
                <div class="panel-body">
                    <div class="stats">${command.stats.matchWinPercentage}% <span>${command.stats.matchWins} Wins</span></div>
                    <canvas id="matches" height="180" width="180"></canvas>
                    <script>

                        var doughnutData = [
                            {
                                value: ${command.stats.matchWins},
                                color:"#1abc9c"
                            },
                            {
                                value : ${command.stats.matchLosses},
                                color : "#F7464A"
                            },
                        ];
                        var options = {
                            //Boolean - Whether we should show a stroke on each segment
                            segmentShowStroke : false,
                            //The percentage of the chart that we cut out of the middle.
                            percentageInnerCutout : 90,

                        };
                        var myDoughnut = new Chart(document.getElementById("matches").getContext("2d")).Doughnut(doughnutData,options);

                    </script>
                </div>
                <div class="panel-heading">
                    <h3 class="panel-title">Games <span class="badge pull-right">${command.stats.totalGames}</span></h3>
                </div>
                <div class="panel-body">
                    <div class="stats">${command.stats.gameWinPercentage}% <span>${command.stats.gameWins} Wins</span></div>
                    <canvas id="games" height="180" width="180"></canvas>
                    <script>

                        var doughnutData = [
                            {
                                value: ${command.stats.gameWins},
                                color:"#1abc9c"
                            },
                            {
                                value : ${command.stats.gameLosses},
                                color : "#F7464A"
                            }

                        ];
                        var options = {
                            //Boolean - Whether we should show a stroke on each segment
                            segmentShowStroke : false,
                            //The percentage of the chart that we cut out of the middle.
                            percentageInnerCutout : 90,

                        };
                        var myDoughnut = new Chart(document.getElementById("games").getContext("2d")).Doughnut(doughnutData,options);

                    </script>
                </div>


            </div>
        </div>
        <div class="col-md-9">
            <div class="table-responsive panel-default">
                <div class="panel-heading"><h3 class="panel-title">Match History</h3></div>
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th>Date</th>
                        <th>Opponent</th>
                        <th></th>
                        <th>Score</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="item" items="${command.matches}" varStatus="loop">
                        <tr>
                            <td>${item.dateString}</td>
                            <td><a href="/profile/${item.p2.id}">${item.p2.name}</a></td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.status=='W'}">
                                        <span class="label label-success">${item.status}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="label label-danger">${item.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${item.p1Score} - ${item.p2Score}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
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
