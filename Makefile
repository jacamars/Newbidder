.PHONY: clean local docker react react-campaigns backup-db restore-db minio

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
	
application: local docs react docker

local: docs
	mvn assembly:assembly -DdescriptorId=jar-with-dependencies  -Dmaven.test.skip=true
	docker build -t jacamars/newbidder .

minio:
	mkdir -p /tmp/s3
	docker-compose -f minio.yml up -d
	bash -c "./wait-for-it.sh localhost:9000 -t 120"
	./tools/copy2s3 "endpoint=http://localhost:9000&aws_access_key=AKIAIOSFODNN7EXAMPLE&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY&bucket=cidr&filename=data/METHBOT.txt&key=METHBOT.txt"
	./tools/copy2s3 "endpoint=http://localhost:9000&aws_access_key=AKIAIOSFODNN7EXAMPLE&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY&bucket=geo&filename=data/zip_codes_states.csv&key=zip_codes_states.csv"
	./tools/copy2s3 "endpoint=http://localhost:9000&aws_access_key=AKIAIOSFODNN7EXAMPLE&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY&bucket=geo&filename=data/adxgeo.csv&key=adxgeo.csv"
	./tools/copy2s3 "endpoint=http://localhost:9000&aws_access_key=AKIAIOSFODNN7EXAMPLE&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY&bucket=config&filename=Campaigns/payday.json&key=payday.json"
	docker-compose -f  minio.yml down

docker:
	docker build -t jacamars/newbidder .
	docker push jacamars/newbidder

backup-db:
	pg_dump -F c -b -v --dbname=postgresql://postgres:postgres@localhost:5432 > database.backup

restore-db:
	pg_restore --dbname=postgresql://postgres:postgres@localhost:5432 --verbose database.backup

docs:
	cd mkdocs && mkdocs build
	rm -r www/docs || true
	cp -ar mkdocs/site www/docs
	
website: docs
	docker build -t jacamars/rtbx-website -f Dockerfile.website .
	docker push jacamars/rtbx-website
	
clean:
	mvn clean

