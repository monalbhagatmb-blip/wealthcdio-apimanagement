Banking Transaction Processor

Scenario:
Create a service that processes banking transactions for multiple accounts.
Requirements:
- Support accounts with unique IDs and balances.
- Allow deposit, withdrawal, and transfer operations.
- Validate operations to prevent overdrafts and invalid amounts.
- Maintain a ledger of transactions per account with timestamps.
- Provide APIs to query account balances and transaction histories.

Things we are looking for:
- It is highly desired that the code has been test driven.
- Clean code.
- Edge case consideration.
- Object orientation.
- Git history.

Final Remarks
1. Please share the private GitHub repository with the “wealthcdio-apimanagement” user, and email us 
the link, so that our team can check it. 
2. Please read this letter (pdf) a couple of times.
3. If you feel stuck, please refer to the “Things we value” section. We deliberately put “Simple”, as the first 
thing in there. 
4. If you are in doubt or pondering options, please refer to the “Things we are looking for” section.
5. Your code is expected to compile as a minimum criterium. If you skip something or make an 
important decision, or your code is not yet made compilable due to time constraint, please add a 
comment explaining the rationale in the README file. We will take them into consideration.

The API routes, defined in BankingController.java, include:

GET http://localhost:8080/api/accounts/ACC-100/balance
GET http://localhost:8080/api/accounts/ACC-100/history
POST http://localhost:8080/api/transactions/deposit?accountId=ACC-100&amount=50.00
POST http://localhost:8080/api/transactions/withdraw?accountId=ACC-100&amount=25.00
POST http://localhost:8080/api/transactions/transfer?sourceAccountId=ACC-100&destinationAccountId=DEST-01&amount=10.00
GET routes can be opened in a browser. For POST routes, use Swagger UI or a terminal, for example:
curl -X POST "http://localhost:8080/api/transactions/deposit?accountId=ACC-100&amount=50.00"