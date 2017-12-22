<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.datastore.*" %>
<%@ page import="java.util.List" %>



<!DOCTYPE html>


<html>
    <head>
        <title>MiniTwitti</title>
        <meta charset="utf-8" />
    </head>

    <body>
        <h1>Ajouter un utilisateur</h1>
        <form action="/home.jsp" method="post">
            <p>
                <label>new user ID : <input type="text" name="userId" /></label>
            </p>
            <p>
                <input type="submit" />
            </p>
        </form>

		<p>
			<!-- récupère le userId -->
			<%=	request.getParameter("userId") %>
		</p>
		
		
        <h1>Faire poster un message à un utilisateur</h1>
		<form action="/home.jsp" method="post">
            <p>
                <label>user ID : <input type="text" name="postingUserId" /></label>
            </p>
            <p>
                <label>message : <input type="text" name="message" /></label>
            </p>
            <p>
                <input type="submit" />
            </p>
        </form>
        
        <p>
			<!-- récupère le postingUserId -->
            <%=	request.getParameter("postingUserId") %>
        </p>
        <p>
			<!-- récupère le message -->
            <%=	request.getParameter("message") %>
        </p>
        
        <h1>Récupérer la TL d'un utilisateur</h1>
        <form action="/home.jsp" method="post">
            <p>
                <label>user ID : <input type="text" name="userIdTimeline" /></label>
            </p>
            <p>
                <input type="submit" />
            </p>
        </form>

		<p>
			<!-- récupère le userIdTimeline -->
			<%=	request.getParameter("userIdTimeline") %>
		</p>
        
    </body>

</html>
