# Seb demo application
**Documentation in progress!!!**


This application should provide REST endpoints manage users permissions

Solution is based on endpoints:

-**login** to provide token for further actions

-**user** to provide interface for managing users

-**role** to provide interface for managing users roles

## User manual
docker-composer will create it

go to project folder:

```
$ docker-compose up -d
```

To check it connection as root:
```
$ docker exec -it mysql57seb mysql -uroot -p
$ docker exec -it mysql57seb mysql -uroot -p -P 3307
```
database root password is `mypassword`

Or check telnet:
```
$ telnet localhost 3306
```

## Improvements:

Add access to database root from everywhere (during development time)

```
mysql> CREATE USER 'root'@'%' IDENTIFIED BY 'mypassword';
mysql> GRANT ALL PRIVILEGES ON *.* TO 'root'@'%';
```
Reload:
```
mysql> FLUSH PRIVILEGES;
```

## Run application:
First build in project folder:
```
$ mvn package
```
Then execute:
```
$ java -jar target/demo-backend-0.0.1-SNAPSHOT.jar
```

## Usage examples:

### Get token:
```
$ curl -d '{"user":"admin", "password":"admin"}' -H "Content-Type: application/json" -X POST http://127.0.0.1:8080/v1/vis-test/login
```
Response:
```
{"token":"7553e3e9-8234-4933-b8cc-587a6d220ecf"}
```

## List available roles:
```
$ curl -H "token: 7553e3e9-8234-4933-b8cc-587a6d220ecf" http://127.0.0.1:8080/v1/vis-test/roles
```
Response:
```
[{"id":1,"name":"CREATE_USER_ROLE","permissions":["CREATE_USERS"]}]
```
## Admin is creating new user with role that exists
```
$ curl -d '{"username": "Max", "password": "maxpassword", "roleId": 1}' -H "Content-Type: application/json" -H "token: 7553e3e9-8234-4933-b8cc-587a6d220ecf" -X POST http://127.0.0.1:8080/v1/vis-test/users
```
Response:
```
{"id":2,"username":"Max","role":{"id":1,"roleName":"CREATE_USER_ROLE"}}
```
User can log in, but doesn't have other permission except create users
```
$ curl -d '{"user":"Max", "password":"maxpassword"}' -H "Content-Type: application/json" -X POST http://127.0.0.1:8080/v1/vis-test/login
```
Response:
```
{"token":"a8c7229d-1c75-4d43-a570-ebafb19b0690"}
```

## Admin is creating new more useful roles
```
$ curl -d '{"name": "MANAGE_USER_ROLE", "permissions":["CREATE_USERS","LIST_USERS","DELETE_USERS","EDIT_USERS"]}' -H "Content-Type: application/json" -H "token: 7553e3e9-8234-4933-b8cc-587a6d220ecf" -X POST http://127.0.0.1:8080/v1/vis-test/roles
$ curl -d '{"name": "LIST_USER_ROLE", "permissions":["LIST_USERS"]}' -H "Content-Type: application/json" -H "token: 7553e3e9-8234-4933-b8cc-587a6d220ecf" -X POST http://127.0.0.1:8080/v1/vis-test/roles
```
Response:
```
{"id":2,"name":"MANAGE_USER_ROLE","permissions":["CREATE_USERS","LIST_USERS","DELETE_USERS","EDIT_USERS"]}
{"id":3,"name":"LIST_USER_ROLE","permissions":["LIST_USERS"]}
```

## Admin is creating new manager user
```
$ curl -d '{"username": "Kate", "password": "katepassword", "roleId": 2}' -H "Content-Type: application/json" -H "token: 7553e3e9-8234-4933-b8cc-587a6d220ecf" -X POST http://127.0.0.1:8080/v1/vis-test/users
$ curl -d '{"username": "Alex", "password": "alexpassword", "roleId": 2}' -H "Content-Type: application/json" -H "token: 7553e3e9-8234-4933-b8cc-587a6d220ecf" -X POST http://127.0.0.1:8080/v1/vis-test/users
```
Responses:
```
{"id":3,"username":"Kate","role":{"id":2,"roleName":"MANAGE_USER_ROLE"}}
{"id":4,"username":"Alex","role":{"id":2,"roleName":"MANAGE_USER_ROLE"}}
```

## Check Kate (manager) can work with users
```
$ curl -d '{"user":"Kate", "password":"katepassword"}' -H "Content-Type: application/json" -X POST http://127.0.0.1:8080/v1/vis-test/login
```
Response:
```
{"token":"27bc79ce-79cb-4662-970b-c3d88e2dac42"}
```
List users:
```
$ curl -H "token: 27bc79ce-79cb-4662-970b-c3d88e2dac42" http://127.0.0.1:8080/v1/vis-test/users
```
Response:
```
[{"id":1,"username":"admin","role":{"id":1,"roleName":"CREATE_USER_ROLE"}},
{"id":2,"username":"Max","role":{"id":1,"roleName":"CREATE_USER_ROLE"}},
{"id":3,"username":"Kate","role":{"id":2,"roleName":"MANAGE_USER_ROLE"}},
{"id":4,"username":"Alex","role":{"id":2,"roleName":"MANAGE_USER_ROLE"}}]
```
Change Max role:
```
$ curl -d '{"roleId": 3}' -H "Content-Type: application/json" -H "token: 27bc79ce-79cb-4662-970b-c3d88e2dac42" -X PUT http://127.0.0.1:8080/v1/vis-test/users/2
```
Response:
```
{"id":2,"username":"Max","role":{"id":3,"roleName":"LIST_USER_ROLE"}}
```

Remove Alex:
```
$ curl -H "Content-Type: application/json" -H "token: 27bc79ce-79cb-4662-970b-c3d88e2dac42" -X DELETE http://127.0.0.1:8080/v1/vis-test/users/4
```
Empty response, but user removed:

```
$ curl -H "token: 27bc79ce-79cb-4662-970b-c3d88e2dac42" http://127.0.0.1:8080/v1/vis-test/users
```
Response:
```
[{"id":1,"username":"admin","role":{"id":1,"roleName":"CREATE_USER_ROLE"}},
{"id":2,"username":"Max","role":{"id":1,"roleName":"CREATE_USER_ROLE"}},
{"id":3,"username":"Kate","role":{"id":2,"roleName":"MANAGE_USER_ROLE"}}]
```
