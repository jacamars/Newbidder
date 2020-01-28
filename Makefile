.PHONY: clean local docker

build: application 

application:
	mvn assembly:assembly -DdescriptorId=jar-with-dependencies  -Dmaven.test.skip=true

pookie:
	mvn assembly:assembly -DdescriptorId=jar-with-dependencies  -Dmaven.test.skip=true
	docker build -t pookie-w:5000/rtb4free:J11 .
	docker push pookie-w:5000/rtb4free:J11

local:
	mvn assembly:assembly -DdescriptorId=jar-with-dependencies  -Dmaven.test.skip=true
	docker build -t newbidder .

docker:
	docker build -t newbidder .


clean:
	mvn clean

