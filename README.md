Whydah-TestWebApp
=================

Reference application that requires log in.

Goto http://localhost:9990/test/hello to trigger log in.

TODO:
* Fix logos / styles to get a Whydah-style



Server overview
===============


Development
===========

http://myApp.net - App using Whydah
http://myserver.net - Whydah SSO

Webproxy CNAME	 					CNAME/direct	
http://myserver.net/huntevaluationbackend/		server-x:8080/huntevaluationbackend
http://myserver.net					http://localhost:8983/solr	
http://myserver.net/sso					http://localhost:9997/sso	
http://myserver/tokenservice				http://localhost:9998/tokenservice/	
http://myserver.net/uib					http://localhost:9995/uib/	
http://myserver.cloudapp.net/useradmin			http://localhost:9996/useradmin/ 		 loop with ssologinservice.


Test/Production
===============
http://myApp.net - App using Whydah
http://myserver.net - Whydah SSO


Webproxy CNAME	 					CNAME/direct	
http://myserver.net/huntevaluationbackend/		server-x:8080/huntevaluationbackend
http://myserver.net					http://server-a:8983/solr	
http://myserver.net/sso					http://server-b:9997/sso	
http://myserver/tokenservice				http://server-c:9998/tokenservice/	
http://myserver.net/uib					http://server-d:9995/uib/	
http://myserver.cloudapp.net/useradmin			http://server-e:9996/useradmin/ 		 loop with ssologinservice.


Development Infrastructure
==========================

Webproxy CNAME	 		CNAME/direct	 	 		Comment
http://mvnrepo.cantara.no	http://nexus.cantara.no:8081		Ask Erik if it doesn't work.
http://ci.cantara.no		http://217.77.36.146:8080/jenkins/		 

