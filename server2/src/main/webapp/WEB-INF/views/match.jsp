<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:set var="pageTitle" value="Lord of the Ping - New Match" scope="request"/>
<c:set var="pageType" value="match" scope="request"/>

<jsp:include page="./header.jsp"/>

<script type="text/javascript">
    $(function () {
        $("#gamesWon").change(function (e) {
            $("#gamesLost").empty();

            var options
            if ($("#gamesWon option:selected").val() == "0") {
                options = ['3', '4', '5' ];
            }

            if ($("#gamesWon option:selected").val() == "1") {
                options = ['3', '4'];
            }

            if ($("#gamesWon option:selected").val() == "2") {
                options = ['3'];
            }

            if ($("#gamesWon option:selected").val() == "3") {
                options = ['0', '1', '2'];
            }

            if ($("#gamesWon option:selected").val() == "4") {
                options = ['0', '1'];
            }

            if ($("#gamesWon option:selected").val() == "5") {
                options = ['0'];
            }

            for(var i=0; i<options.length;i++ ){
                var newOption = document.createElement("option");
                newOption.value=options[i];
                newOption.innerHTML = options[i];

                $("#gamesLost").append(newOption);
            }

            $('#gamesLost').prop('disabled', false);
        });
    });
</script>


<div class="container">
    <h2>New Match (Best of 5)</h2>
    <form:form commandName="command" action="/match/save" method="post" class="form-horizontal" role="form">
        <form:errors path="*" element="div" cssClass="alert alert-error"/>
        <div class="form-group">
            <label class="col-sm-2 control-label">Who did you play?</label>
            <div class="col-sm-10">
                <form:input path="email" class="form-control" id="exampleInputEmail1" placeholder="Opponent" spellcheck="false" autocomplete="off" autofocus="" />
                <script>
                    $(document).ready(function(){
                        $("#exampleInputEmail1").typeahead({
                            source: ${command.emailList}
                        });
                    });
                </script>
            </div>
        </div>
        <div class="form-group">
            <label for="exampleInputEmail1" class="col-sm-2 control-label">Games You Won</label>
            <div class="col-sm-10">
                <form:select class="form-control" path="gamesWon" multiple="false" items="${command.values}" />
            </div>
        </div>
        <div class="form-group">
            <label for="exampleInputEmail1" class="col-sm-2 control-label">Games You Lost</label>
            <div class="col-sm-10">
                <form:select class="form-control" path="gamesLost"  multiple="false" items="${command.values}" disabled="true" />
            </div>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button type="submit" class="btn btn-primary">Submit</button>
            </div>
        </div>
        <form:hidden path="emailList" />
    </form:form>

</div> <!-- /container -->
<script src="/js/bootstrap-typeahead.js"></script>
<jsp:include page="./footer.jsp"/>
