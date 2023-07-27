
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Form</title>
</head>
<body>
    <form action="/ServletDemo_war/users" method="post">
        <label for="name">Name:</label><br>
        <input type="text" id="name" name="name"><br>
        <label for="surname">Surname:</label><br>
        <input type="text" id="surname" name="surname"><br>
        <label for="age">Age:</label><br>
        <input type="number" id="age" name="age"><br>
        <input type="submit" value="Submit">
    </form>
</body>
</html>
