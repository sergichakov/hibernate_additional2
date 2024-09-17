<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
    <meta charset="UTF-8">
</head>
<body>
<h1>Пролог
</h1>
<div>Ты стоишль в коспическом порту и готов подняться на борт своего корабля. Так что вперед!</div>
<br>
<div>
<form  ${hideForm} >
   <input type="text" id="field1" name="userName" maxlength="8" size="10"/>
    <input type="submit" value="Представиться"/>
</form></div>
<button type="button" onclick="window.location.href='/question?page=1'">Начать Игру</button>
<br>Статистика<br>
IP address: ${ipAddress}<br>
Имя игрока: ${playerName}<br>
Количество пройденных игр: ${numberOfGames}


</body>
</html>