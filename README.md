## Revolut test task: Money Transfer

Service with REST API for money transfer.

### How to build
    gradle jar
    
### How to run on localhost:4567
    java -jar ./build/libs/moneytransfer-1.0-SNAPSHOT.jar
    
## API Endpoints

| Description | Request | Path | Response | Example |
| ------ | ------ | ------ | ------ | ------ |
| Create new account with balance | POST | /accounts | JSON with info about account | Input: [{"balance": 1000}]; Output: [{"id": 1, "balance": 100}] |
| Get account by id | GET | /accounts/{id} | JSON with info about account | Output: [{"id": 1, "balance": 1000}] |
| Get accounts | GET | /accounts | JSON with info about accounts | Output: [{"id":1,"balance":1000},{"id":2,"balance":1000},{"id":3,"balance":3000}]
| Transfer money | POST | /transfer | JSON with info about accounts | Input: [{"fromAccountId": 1, "toAccountId": 2, "amount": 100}]; Output: [{"fromAccount": {id: 1, balance: 900}, toAccount: {id: 2, balance: 1100}}]|
