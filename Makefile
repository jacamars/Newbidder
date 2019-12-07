.PHONY: clean local pookie

build: application 

application:
	mvn assembly:assembly -DdescriptorId=jar-with-dependencies  -Dmaven.test.skip=true

pookie:
	mvn assembly:assembly -DdescriptorId=jar-with-dependencies  -Dmaven.test.skip=true
	docker build -t pookie-w:5000/rtb4free:J11 .
	docker push pookie-w:5000/rtb4free:J11

local:
	mvn assembly:assembly -DdescriptorId=jar-with-dependencies  -Dmaven.test.skip=true
	docker build -t rtb4free:J11 .



clean:
	mvn clean

