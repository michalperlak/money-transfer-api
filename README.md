# money-transfer-api

[![Build Status](https://travis-ci.org/michalperlak/money-transfer-api.svg?branch=master)](https://travis-ci.org/michalperlak/money-transfer-api)

## Requirements

- JRE 8 or higher

## Running

Run server:

`./gradlew run`

Build and run executable jar:

`./gradlew build`

`java -jar money-transfer-web-api/build/libs/money-transfer-web-api-1.0-SNAPSHOT-all.jar`

## Tech stack

 - Kotlin
 - Reactor Netty
 - Arrow KT
 - Moshi
 - JUnit 5
 - RestAssured
 
## Project structure 

The project consist of 3 modules/layers:
 - money-transfer-core -> core domain model and logic, dependency to ArrowKT only
 
 - money-transfer-app -> translation between external model and internal domain, 
 input validation, persistence (in memory), dependencies to JSON serialization library (Moshi) and Reactor model
 
 - money-transfer-web-api -> HTTP server exposing REST API, dependency to Reactor Netty
 
## REST API 

### Accounts

#### Create new account

`POST /api/accounts`

Request body:

Required attributes:

|Name|Data type|Constraints|
|------|-------|-----------|
| ownerId| string | Must be a valid UUID |
| currency| string| Must be a valid currency code (only two values are currently accepted: PLN and EUR) |

Example:
```json
{
	"ownerId": "69946ab4-5a7c-4f0b-b4fe-41aeda9ab17b",
	"currency": "PLN"
}
```

Response:

|Status|Headers| Body| Description|
|------|-------|-----|------------|
|201| Location: /api/accounts/{accountId}| - | Account created successfully. |
|400| - | { "error": message } | Invalid owner id or currency code |


#### Get account

`GET /api/accounts/{accountId}`

Required path variables:

|Name|Data type|Constraints|
|------|-------|-----------|
| accountId| string| Must be a valid UUID |

Response:

|Status| Body| Description|
|------|-----|------------|
|200 | Account details | Details of the account. |
|400 | { "error": message } | Invalid account id |
|404 | { "error": message } | Account with id not found |

Example success response:

```json
{
    "id": "624a66b3-51b1-4e54-9e54-4b4787c3d3ea",
    "currency": "PLN",
    "ownerId": "69946ab4-5a7c-4f0b-b4fe-41aeda9ab17b",
    "balance": "0.00"
}
```

### Transactions

#### Create new transaction - deposit

`POST /api/transactions`

Request body:

Required attributes:

|Name|Data type|Constraints|
|------|-------|-----------|
| type| string | Transaction type - DEPOSIT |
| amount| string | Must be a valid number |
|destinationAccountId| string| Must be a valid account id|

Example request body:

```json
{
	"type": "DEPOSIT",
	"amount": "1000",
	"destinationAccountId": "329ceea2-03cf-4eba-b5d6-c6ca81b70dc5"
}
```

Response:

|Status| Body| Description|
|------|-----|------------|
|200| Transaction details | Transaction processed successfully. |
|400| { "error": message } | Invalid amount |
|400| { "error": message } | Destination account id invalid or not found  |

Example success response:

```json
{
    "transactionId": "5ad4fc49-cda5-4967-8f10-973c822dd3c4",
    "type": "DEPOSIT",
    "amount": "1000.00",
    "destinationAccountId": "329ceea2-03cf-4eba-b5d6-c6ca81b70dc5"
}
```

#### Create new transaction - transfer

`POST /api/transactions`

Request body:

Required attributes:

|Name|Data type|Constraints|
|------|-------|-----------|
| type| string | Transaction type - TRANSFER |
| amount| string | Must be a valid number |
|sourceAccountId| string| Must be a valid account id|
|destinationAccountId| string| Must be a valid account id|

Example request body:

```json
{
	"type": "TRANSFER",
	"amount": "300",
	"sourceAccountId": "329ceea2-03cf-4eba-b5d6-c6ca81b70dc5",
	"destinationAccountId": "b5213df1-9fc6-4e62-9d30-f0a2b4f96c74"
}
```

Response:

|Status| Body| Description|
|------|-----|------------|
|200| Transaction details | Transaction processed successfully. |
|400| { "error": message } | Invalid amount or insufficient funds |
|400| { "error": message } | Source/Destination account id invalid or not found  |

Example success response:

```json
{
    "transactionId": "c50a2a4a-4e09-4486-8e5d-fd320f54ddb6",
    "type": "TRANSFER",
    "amount": "300.00",
    "destinationAccountId": "b5213df1-9fc6-4e62-9d30-f0a2b4f96c74",
    "sourceAccountId": "329ceea2-03cf-4eba-b5d6-c6ca81b70dc5"
}
```
