<<<<<<< HEAD
.PHONY: clean local docker react

build: application react docker

react:
	cd react/control && yarn install
	cd react/control && npm run-script build
	rm -r www/control || true
	cp -a react/control/build www
	mv www/build www/control
	cd react/exchange && yarn install
	cd react/exchange && npm run-script build
	rm -r www/exchange || true
	cp -a react/exchange/build www
	mv www/build www/exchange
	cd react/campaigns && npm run-script build
	rm -r www/campaigns || true
	cp -a react/campaigns/build www
	mv www/build www/campaigns
	
	
application:
	mvn assembly:assembly -DdescriptorId=jar-with-dependencies  -Dmaven.test.skip=true

=======
.PHONY: clean local docker

build: application 

application:
	mvn assembly:assembly -DdescriptorId=jar-with-dependencies  -Dmaven.test.skip=true

pookie:
	mvn assembly:assembly -DdescriptorId=jar-with-dependencies  -Dmaven.test.skip=true
	docker build -t pookie-w:5000/rtb4free:J11 .
	docker push pookie-w:5000/rtb4free:J11

>>>>>>> 1ed300a25d45ec953eba229f9cb52ade266fb891
local:
	mvn assembly:assembly -DdescriptorId=jar-with-dependencies  -Dmaven.test.skip=true
	docker build -t newbidder .

docker:
	docker build -t newbidder .


clean:
	mvn clean

