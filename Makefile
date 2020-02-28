.PHONY: clean local docker react react-control react-exchange react-campaigns backup-db restore-db

build: application

react-control:
	cd react/control && npm update
	cd react/control && yarn install
	cd react/control && npm run-script build
	rm -r www/control || true
	cp -a react/control/build www
	mv www/build www/control
	
react-exchange:
	cd react/exchange && npm update
	cd react/exchange && yarn install
	cd react/exchange && npm run-script build
	rm -r www/exchange || true
	cp -a react/exchange/build www
	mv www/build www/exchange
	
react-campaigns:
	cd react/campaigns && npm update
	cd react/campaigns && yarn install
	cd react/campaigns && npm run-script build
	rm -r www/campaigns || true
	cp -a react/campaigns/build www
	mv www/build www/campaigns
	
react: react-exchange react-control react-campaigns

	
application: local react docker

local:
	mvn assembly:assembly -DdescriptorId=jar-with-dependencies  -Dmaven.test.skip=true

docker:
	docker build -t jacamars/newbidder .

backup-db:
	pg_dump -F c -b -v --dbname=postgresql://postgres:postgres@localhost:5432 > database.backup

restore-db:
	pg_restore --dbname=postgresql://postgres:postgres@localhost:5432 --verbose database.backup

clean:
	mvn clean

