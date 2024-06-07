### Custom JWT generator/validator example
* `/authenticate`: authenticate user and returns token. Following Request body is needed for test
```
{
	"username": "tester1",
	"password": "tester1pw"
}
```
* `/refresh`: take refresh token and return new token. Following Request body is needed.
```
{
    "refreshToken": {Refresh token that has been provied from /authenticate response}
}
```
* `/hello`: only accessible with valid access toke. Following Http Header is needed for test
```
Authorization: Bearer {Token you got from /authenticate service}
```

#### Dependencies
Following dependencies are needed
```groovy
dependencies {
	compile 'org.springframework.boot:spring-boot-starter'
	compile 'org.springframework.boot:spring-boot-starter-web'
	compile 'org.springframework.boot:spring-boot-starter-security'
	compile 'io.jsonwebtoken:jjwt-api:0.11.2'
    compile 'io.jsonwebtoken:jjwt-impl:0.11.2'
    compile 'io.jsonwebtoken:jjwt-jackson:0.11.2'
}
```

### Package Structure
`brian.boot.jwt.config`: has Security Configuration, Filter, and Jwt Utility class
`brian.boot.jwt.controller`: has RestController to provide the end point
`brian.boot.jwt.model`: has Request and Response data model
`brian.boot.jwt.service`: has simple login check