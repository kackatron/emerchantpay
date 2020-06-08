# eMerchant Payment System

##Build
In order to build this project you have to set a backend data source.
Currently the settings are for MySQL InnoDB engine. 
You can find the settings for the datasource in application.properties file.
Setup your database url and user creds there:
``` 
spring.datasource.url=jdbc:mysql://localhost:3306/payment_system?useUnicode=yes&characterEncoding=UTF-8&useSSL=false&serverTimezone=UTC&useLegacyDatetimeCode=false
spring.datasource.username=root
spring.datasource.password=changeit
```
Property 
```
spring.jpa.hibernate.ddl-auto=update
```
Controls how JPA will handle the Database when the contexts of the  application load.
If it is set to create it will always recreate the tables in the schema.
This will allow you to execute the tests of the project.
```
Build with tests:
./gradlew clean build
Build without test:
./gradlew clean build -x test
```
##Purpouse

This project is a POC for a payment system.
It has two type of users - Administrators and Merchants.
They are both loaded from CSV file, at the startup of the runtime. 
This behaviour might change in the future.

Administrators have the authority to delete Merchants.
While Merchant can register transactions.
This happens by REST Endpoint dedicated to this : 
```
http://hostnama:port/trx/load
```
Before that the Merchant have to acquire JWT token.
This happens by authenticating  before : 
```
http://hostnama:port/api/auth/authenticate
```
By applying the token to its request the Merchant can register transaction.
There are four types of transactions :
Authorization - checks and reserves the amount to be paid
Charge - actually takes the amount
Revert - used to Revert the Charge transaction if needed.
Reversal - used to cancel the Authorize transaction if needed.

There are two schedule jobs
Processing - It goes through all the transactions and their referent ones 
and calculates, total transaction amount for each Merchant. Charge transaction 
adds up, while Revert subtracts from the whole amount. After it  passes 
through the transactions they are marked as processed and can be safely deleted.
Cleanup - It goes through all the processed transactions older that one  hour and 
deletes them.