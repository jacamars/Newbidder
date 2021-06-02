.PHONY: clean local docker react react-campaigns backup-db restore-db minio realS3

build: application

help:
	@printf "\n"
	@printf "make application		Builds the entire application\n"
	@printf "make campaigns			Builds the react campaigns application\n"
	@printf "make docs			Builds the documentation\n"
	@printf "make docker			Creates a jacamars/newbidder docker image\n"
	@printf "make restore-db			Restores the postgres database from the previously saved db\n"
	@printf "make backup-db			Saves the current db\n"
	@printf "make minio			Makes a minio s3 setup in /tmp\n"
	@printf "make realS3			Makes a real s3 setup\n"
	@printf "\n"
	
react-campaigns:
	rm react/campaigns/package-lock.json|| true
	rm react/campaigns/yarn.lock || true
	cd react/campaigns && npm update
	cd react/campaigns && yarn install
	cd react/campaigns && npm run-script build
	rm -r www/campaigns || true
	cp -a react/campaigns/build www
	mv www/build www/campaigns
	
react: react-campaigns
	
application: react local docker

local: 
	mvn clean package -Dmaven.test.skip=true
	docker build -t jacamars/newbidder .

minio:
	mkdir -p /tmp/s3
	docker-compose -f minio.yml up -d
	bash -c "./wait-for-it.sh localhost:9000 -t 120"
	./tools/copy2s3 "endpoint=http://localhost:9000&aws_access_key=AKIAIOSFODNN7EXAMPLE&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY&bucket=rtb4free-big-data&filename=data/METHBOT.txt&key=cidr/METHBOT.txt"
	./tools/copy2s3 "endpoint=http://localhost:9000&aws_access_key=AKIAIOSFODNN7EXAMPLE&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY&bucket=rtb4free-big-data&filename=data/zip_codes_states.csv&key=geo/zip_codes_states.csv"
	./tools/copy2s3 "endpoint=http://localhost:9000&aws_access_key=AKIAIOSFODNN7EXAMPLE&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY&bucket=rtb4free-big-data&filename=data/adxgeo.csv&key=geo/adxgeo.csv"
	./tools/copy2s3 "endpoint=http://localhost:9000&aws_access_key=AKIAIOSFODNN7EXAMPLE&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY&bucket=rtb4free-big-data&filename=Campaigns/payday.json&key=config/payday.json"
	./tools/copy2s3 "endpoint=http://localhost:9000&aws_access_key=AKIAIOSFODNN7EXAMPLE&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY&bucket=rtb4free-big-data&filename=data/audience.txt&key=bloom/audience1/test-audience.txt"
	./tools/copy2s3 "endpoint=http://localhost:9000&aws_access_key=AKIAIOSFODNN7EXAMPLE&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY&bucket=rtb4free-big-data&filename=www/images/320x50.jpg&key=images/320x50/tunein.jpg"
	./tools/copy2s3 "endpoint=http://localhost:9000&aws_access_key=AKIAIOSFODNN7EXAMPLE&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY&bucket=rtb4free-big-data&filename=www/contact.html/&key=landing/rtb4free/contact.html"
	./tools/copy2s3 "endpoint=http://localhost:9000&aws_access_key=AKIAIOSFODNN7EXAMPLE&aws_secret_key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY&bucket=rtb4free-big-data&filename=www/images/trump.mp4&key=video/trump.mp4"
	
realS3:
	./tools/copy2s3 "aws_access_key=$(aws_access_key)&aws_secret_key=$(aws_secret_key)&aws_region=$(aws_region)&bucket=$(bucket)&filename=data/METHBOT.txt&key=cidr/METHBOT.txt"
	./tools/copy2s3 "aws_access_key=$(aws_access_key)&aws_secret_key=$(aws_secret_key)&aws_region=$(aws_region)&bucket=$(bucket)&filename=data/zip_codes_states.csv&key=geo/zip_codes_states.csv"
	./tools/copy2s3 "aws_access_key=$(aws_access_key)&aws_secret_key=$(aws_secret_key)&aws_region=$(aws_region)&bucket=$(bucket)&filename=data/adxgeo.csv&key=geo/adxgeo.csv"
	./tools/copy2s3 "aws_access_key=$(aws_access_key)&aws_secret_key=$(aws_secret_key)&aws_region=$(aws_region)&bucket=$(bucket)&filename=Campaigns/payday.json&key=config/payday.json"
	./tools/copy2s3 "aws_access_key=$(aws_access_key)&aws_secret_key=$(aws_secret_key)&aws_region=$(aws_region)&bucket=$(bucket)&filename=data/audience.txt&key=bloom/audience1/test-audience.txt"
	./tools/copy2s3 "aws_access_key=$(aws_access_key)&aws_secret_key=$(aws_secret_key)&aws_region=$(aws_region)&bucket=$(bucket)&filename=www/images/320x50.jpg&key=images/320x50/tunein.jpg"
	./tools/copy2s3 "aws_access_key=$(aws_access_key)&aws_secret_key=$(aws_secret_key)&aws_region=$(aws_region)&bucket=$(bucket)&filename=www/contact.html/&key=landing/rtb4free/contact.html"
	./tools/copy2s3 "aws_access_key=$(aws_access_key)&aws_secret_key=$(aws_secret_key)&aws_region=$(aws_region)&bucket=$(bucket)&filename=www/images/trump.mp4&key=video/trump.mp4"


docker:
	docker build -t jacamars/newbidder .
	docker push jacamars/newbidder
	
push:
	docker push jacamars/newbidder

backup-db:
	pg_dump -F c -b -v --dbname=postgresql://postgres:postgres@localhost:5432 > database.backup

restore-db:
	pg_restore --dbname=postgresql://postgres:postgres@localhost:5432 --verbose database.backup
	
clean:
	mvn clean

