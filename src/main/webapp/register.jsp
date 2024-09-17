<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Registration form</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>
</h1>
<div>Представьтесь. current Username=${ObjectUserName} ${pleaseEnterUserNamePassword}</div>
<br>
<div>
<form>
    <label for="field1">User name</label><br>
   <input type="text" id="field1" name="userName" maxlength="8" size="10" required/><br>
    <label for="password">Password</label><br>
    <input type="password" id="password" name="password"  title="Must contain at least one number" required/><br>
    <input type="submit" value="Представиться"/>
</form></div>
<button type="button" onclick="window.location.href='/start?pageNumber=1&pageSize=3'">Go to task list</button>


</body>
</html>