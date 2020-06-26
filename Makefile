.PHONY: clean local docker react react-campaigns backup-db restore-db

build: application

react-campaigns:
	rm react/campaigns/package.lock || true
	rm react/campaigns/yarn.lock || true
	cd react/campaigns && npm update
	cd react/campaigns && yarn install
	cd react/campaigns && npm run-script build
	rm -r www/campaigns || true
	cp -a react/campaigns/build www
	mv www/build www/campaigns
	
react: react-campaigns

	
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

