# bol

The [Spring Boot](https://projects.spring.io/spring-boot/) backend for [mancalive](https://secure-cliffs-35079.herokuapp.com/auth), a realtime mancala experiment.

## Requirements

- Java 1.8+
- Maven 3.x

## Building the project
```bash
mvn package
```

## Running the project
```bash
java -jar target/bol-0.0.1-SNAPSHOT.jar
```

### Author

Joel Hernández ([@lifenautjoe](www.lifenautjoe.com))

### FAQ

#### Where's the frontend code?
The project contains the "built" frontend in `src/main/resources/static`.

The frontend code reposiotyr is [here](https://github.com/lifenautjoe/bol-www/).

### License

MIT
