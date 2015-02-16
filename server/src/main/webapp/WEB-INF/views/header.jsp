<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="/img/favicon.png">

    <title><c:out value="${pageTitle}"/> </title>
    <!-- Bootstrap core CSS -->
    <link href="/css/bootstrap.css" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="/css/custom.css" rel="stylesheet">
    <link href="/css/flat-ui.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
</head>

<body>

<script>

    $(document).ready(function (){
        $.ajax({
            type: 'GET',
            url: 'http://www.lordoftheping.com/tt/pending/count',
            dataType: 'json',
            success: finished //Change to this
        });
    });

    function finished(result) {
        $('#inbox').append(result);
    };

</script>

<!-- Static navbar -->
<div class="navbar navbar-inverse navbar-static-top" role="navigation">
    <sec:authorize access="authenticated" var="authenticated"/>
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand text-hide" href="/">Lord of the Ping</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">

                <c:choose>
                    <c:when test="${pageType=='home'}">
                        <li class="active"><a href="/">Leaderboard</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="/">Leaderboard</a></li>
                    </c:otherwise>
                </c:choose>

                <c:choose>
                    <c:when test="${pageType=='match'}">
                        <li class="active"><a href="/match">New Match</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="/match">New Match</a></li>
                    </c:otherwise>
                </c:choose>

                <c:choose>
                    <c:when test="${pageType=='profile' && ownProfile == 'true'}">
                        <li class="active"><a href="/profile/myProfile">Profile</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="/profile/myProfile">Profile</a></li>
                    </c:otherwise>
                </c:choose>

                <c:choose>
                    <c:when test="${pageType=='inbox'}">
                        <li class="active"><a href="/inbox">Inbox <span class="badge badge-location" id="inbox"></span></a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="/inbox">Inbox <span class="badge badge-location" id="inbox"></span></a></li>
                    </c:otherwise>
                </c:choose>

            </ul>

            <ul class="nav navbar-nav navbar-right">
            <c:choose>
                <c:when test="${authenticated}">
                    <li id="greeting"><a>Hi <sec:authentication property="principal.name" />!</a></li>
                    <li><a id="navLo1goutLink" href="/logout">Logout</a></li>
                </c:when>
                <c:otherwise>
                    <li><a href="/signin">Sign In/Register</a></li>
                </c:otherwise>
            </c:choose>
            </ul>
        </div>
    </div>
</div>