Example of executable jar built with maven
==========================================

Building jar
------------

* Build jar package and copy all dependencies into target/lib (and modify classpath):
		
		mvn clean package

* Build jar package and embedd all dependencies:
		
		mvn clean package -Dembedd-dependencies

Runing jar
----------
	java -jar target/executable-jar-0.0.1-SNAPSHOT.jar

Donation
--------

I hope you found here something useful and/or interesting.
Help keep this repository growing in more and better projects. 

<a href='http://www.pledgie.com/campaigns/22261'><img alt='Click here to lend your support to: mendlik-open-repository and make a donation at www.pledgie.com !' src='http://www.pledgie.com/campaigns/22261.png?skin_name=chrome' border='0' /></a>
