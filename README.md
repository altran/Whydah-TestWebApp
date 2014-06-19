Whydah-TestWebApp
=================

Reference application that requires log in. The ImplementationExamples includes example code for Whydah integration for JavaScript, Django and Microsoft Sharepoint.

Goto http://localhost:9990/test/hello to trigger log in.

TODO:
* Fix logos / styles to get a Whydah-style

![Architectural Overview](https://raw2.github.com/altran/Whydah-SSOLoginWebApp/master/Whydah%20infrastructure.png)

Installation
============



* create a user for the service
* run start_service.sh


Verify instance:
*  http://server:9998/tokenservice/application.wadl

If you have enabled test-page in the properties, you can run and verify the key 
services from the testpage application (testpage=true)
* http://server:9998/tokenservice/testpage


Developer info
==============

* https://wiki.cantara.no/display/iam/Architecture+Overview
* https://wiki.cantara.no/display/iam/Key+Whydah+Data+Structures
* https://wiki.cantara.no/display/iam/Modules

