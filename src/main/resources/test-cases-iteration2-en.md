# Test Cases — Iteration 2

Test cases are designed based on the Iteration 2 API tests (`ChangeTheUserName.java`, `DepositMoney.java`, `TransferMoney.java`) and the following business requirements:

1. Profile name must be two words, letters only (mandatory)
2. Maximum deposit amount — 5000
3. Maximum transfer amount — 10000

**Precondition for all cases below:** the user is registered and logged in, and has at least one open account (see a separate set of `TC-AUTH-xx` cases for registration/login — not duplicated in each case below).

---

## 1. Change Username

| ID | Scenario | Steps (UI) | Expected Result | Corresponding API Test |
|---|---|---|---|---|
| TC-UN-01 | Successful name change | Open profile → enter "John Smith" in the "Name" field → save | Name is updated to "John Smith", displayed in the profile | `updateCustomerProfileTest` |
| TC-UN-02 | Verify persistence after a successful change | Refresh/reopen the profile page | "John Smith" remains saved | `verifyCustomerProfileAfterUpdating` |
| TC-UN-03 | Single-word name | Enter "John" → save | Error "Name must contain two words with letters only", name not changed | `updateCustomerProfileByAddingJustOneNameTest` |
| TC-UN-04 | Verify name unchanged after an invalid single-word attempt | Reopen profile | Name remains "John Smith" | `verifyCustomerProfileAfterAddingJustOneName` |
| TC-UN-05 | Name containing digits | Enter "John12345" → save | Same validation error, name not changed | `updateCustomerProfileByAddingNumbersInNameTest` |
| TC-UN-06 | Verify name unchanged after an attempt with digits | Reopen profile | Name remains "John Smith" | `verifyCustomerProfileAfterAddingNumbersInNameName` |
| TC-UN-07 | Empty name | Clear the "Name" field → save | Validation error, name not changed | `updateCustomerProfileWithEmptyName` |
| TC-UN-08 | Verify name unchanged after empty input | Reopen profile | Name remains "John Smith" | `verifyCustomerProfileAfterWithEmptyName` |
| TC-UN-09 | Change name without authorization | Log out / session expired → attempt to change name | 401 / redirect to login, change not applied | `updateCustomerProfileWithoutAuthorizationTest` |
| TC-UN-10 | Verify name unchanged after an unauthorized attempt | Log back in → open profile | Name remains "John Smith" | `verifyCustomerProfileWithoutAuthorization` |

---

## 2. Deposit Money

| ID | Scenario | Steps (UI) | Expected Result | Corresponding API Test |
|---|---|---|---|---|
| TC-DEP-01 | Deposit at the maximum allowed amount (5000) | Open account → enter amount 5000 → confirm | Balance = 5000, operation successful | `depositFiveThousandPositiveTest` |
| TC-DEP-02 | Verify transaction history after a 5000 deposit | Open account transaction history | Entry present: account id, amount 5000, type DEPOSIT | `verifyAccountTransactionsAfterDepositingFiveHundred` |
| TC-DEP-03 | Deposit without authorization | Session expired → attempt to deposit 5000 | 401, deposit not performed | `depositWithoutAuthorizationTest` |
| TC-DEP-04 | View transaction history without authorization | Session expired → open transaction history | 401 / redirect to login | `verifyAccountTransactionsAfterWithoutAuthorization` |
| TC-DEP-05 | Deposit amount above the limit (5000.1) | Enter 5000.1 → confirm | "Bad Request" error / limit-exceeded message, balance unchanged | `userCannotDepositInvalidAmountTest` (parameter 5000.1) |
| TC-DEP-06 | Deposit a negative amount (-1) | Enter -1 → confirm | "Bad Request" error, balance unchanged | `userCannotDepositInvalidAmountTest` (parameter -1) |
| TC-DEP-07 | Verify no invalid transactions appear in history | Open transaction history after TC-DEP-05/06 steps | No entries with amount 5000.1 or -1 in history | `verifyTransactionsAfterInvalidDeposit` |

---

## 3. Transfer Money Between Accounts

| ID | Scenario | Steps (UI) | Expected Result | Corresponding API Test |
|---|---|---|---|---|
| TC-TR-01 | Successful transfer within the limit (9999.99) | Top up account 1 to the required balance → transfer 9999.99 to account 2 | "Transfer successful" message displayed, balances updated | `transferMoneyFromTheFirstAccountToTheSecondAccountTest` |
| TC-TR-02 | Verify sender account transaction history after transfer | Open transaction history for account 1 | Transfer entry present | `verifyAccountTransactionAfterTransfer` |
| TC-TR-03 | Transfer at the maximum boundary amount (10000.00) | Transfer 10000.00 to account 2 | Operation successful | `transferMoneyFromTheFirstAccountToTheSecondAccountMaxValueTest` |
| TC-TR-04 | Transfer amount above the limit (10000.01) | Transfer 10000.01 | Error "Transfer amount cannot exceed 10000", transfer not performed | `transferMoneyFromTheFirstAccountToTheSecondAccount_10000_01` |
| TC-TR-05 | Transfer to a non-existing account | Enter receiver id 999, amount 250.75 | Error "Invalid transfer: insufficient funds or invalid accounts" | `transferMoneyToNonExistingAccountTest` |
| TC-TR-06 | Verify no transaction with the non-existing account appears in history | Open sender account transaction history | No entries where relatedAccountId = 999 | `verifyTransferMoneyToNonExistingAccountTest` |
| TC-TR-07 | Deposit as a precondition for transfer (repeated top-ups) | Top up account with 5000, then again with 5000, 500, etc. | Balance correctly accumulates after each top-up | `depositFiveTest`, `depositOneMoreTimeFiveThousandTest`, `depositOneMoreTimeFiveThousandPositiveTest`, `depositFiveHundredPositiveTest`, `depositFiveThousandPositiveTest` |
| TC-TR-08 | Verify the count of DEPOSIT transactions after a series of top-ups | Open transaction history, count entries of type DEPOSIT | Number of DEPOSIT entries matches the number of top-ups performed (3, 5, 6 as the scenario progresses) | `verifyAccountTransactionsAfterDepositing_Id1`, `verifyAccountTransactionsAfterDepositingFiveHundred`, `verifyAccountTransactionsAfterDepositing` |

