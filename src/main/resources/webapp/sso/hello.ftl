<html>
<head>
  <title>Hello from Whydah Test WebApp</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
Hi ${realname}
<br/>
<br/>
You have been logged on to Whydah.<br/>
<br/>
<a href="${logouturl}">Log out</a><br/>
<br/>
We have registered the following privileges on your user:
<br/>
<br/>
<TEXTAREA NAME="usercredential" COLS=150 ROWS=50>${token}</TEXTAREA>
</body>
</html>