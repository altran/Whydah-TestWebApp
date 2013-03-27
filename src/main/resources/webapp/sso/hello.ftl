<html>
<head>
  <title>Hello from SSOTestWebApp</title>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body>
Hei ${realname}
<br/>
<br/>
Du har n&aring; logget p&aring; OBOS SSO.<br/>
<br/>
<a href="${logouturl}">Log out</a><br/>
<br/>
Vi har registrert f&oslash;lgende rettigheter p&aring; din bruker:
<br/>
<br/>
<TEXTAREA NAME="usercredential" COLS=150 ROWS=50>${token}</TEXTAREA>
</body>
</html>