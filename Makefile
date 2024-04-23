run_postgres_db:
	docker run --name projectDB -p 5432:5432 -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin -e POSTGRES_DB=projectDB -d postgres

stop_postgres_db:
	docker stop projectDB
	docker rm projectDB

create_docker_image:
	mvn spring-boot:build-image
