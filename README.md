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

## Usage examples:

### Get token:
```
$ curl -d '{"user":"admin", "password":"admin"}' -H "Content-Type: application/json" -X POST http://127.0.0.1:8080/v1/vis-test/login
```
Response:
```
{"token":"6266cbb7-9bf1-45a0-901e-136c2aafc435"}
```


