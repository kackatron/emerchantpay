# emerchantpay

In order to build this project you have to set a backend data source.
Currently the settings are for MySQL InnoDB engine. 
You can find the settings for the datasource in application.properties file.
setup your database url and user creds there:
 
spring.datasource.url=jdbc:mysql://localhost:3306/payment_system?useUnicode=yes&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false
spring.datasource.username=root
spring.datasource.password=changeit

This will allow you to execute the tests of the project.
Build with tests:
./gradlew clean build
Build without test:
./gradlew clean build -x test
