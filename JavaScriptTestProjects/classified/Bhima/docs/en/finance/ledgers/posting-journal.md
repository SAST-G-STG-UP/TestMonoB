# Journal Module

The Journal is the central module of the BHIMA software - all transactions must flow through the Journal to enter the [General Ledger](/general-ledger.md) and appear in subsequent reports.  It is a gatekeeper for all proposed transactions, a ledger where the accountant can validate, correct, and approve transactions that enter the system.  No financial transaction is considered finalized until it is _posted_ from the Journal into the General Ledger.

## Financial Operations as Transactions

In the introduction, we noted that all financial operations are represented as both a record and a transaction.  In this section, we will discuss the properties of transactions in BHIMA.

As described in [Double Entry Bookkeeping](/finance-modules/overview.md#double-entry-bookkeeping), transactions are composed of two or more lines.  Some information, such as the transaction date is shared across all lines; others, like the accounts, are specific to a line.  The list below contains all properties of a transaction.  Shared properties are denoted by the tag **\[shared\]**.

* **ID**: used only for internal purposes.  This 36 character string uniquely identifies the line in the transaction.  It is really only meant to be used when reporting issues to BHIMA support.
* **Period \[shared\]**: a human readable version of the period.
* **Project \[shared\]**: the project associated with the record.
* **Transaction ID \[shared\]**: a human readable identifier associated with the transaction.  It is composed in the following way: `${project abbreviation}${increment}`.  For example, the first transaction of a project abbreviated by "TST" will be `TST1`.  This allows transactions between projects to be differentiated.
* **Transaction Date \[shared\]**: the date the transaction was created.
* **Record \[shared\]**: The identifier of the record which created this transaction.  These identifiers are composed as follows: `${record type}.${project abbreviation}.${increment}`.  The "record type" is `VO` for Vouchers, `CP` for Cash Payments, and `IV` for invoices.  An example record is `CP.TST.1` , which reads "the first Cash Payment of project TST".
* **Description**: a textual description of the transaction.  Descriptions are either created manually \(such as a voucher description\) or are generated by the application.
* **Account**
* **Debit**: the debit value in the currency of the enterprise.
* **Credit**: the credit value in the currency of the enterprise.
* **Currency \[shared\]**: the currency of the original record.
* **Debit \(Source\)**: the debit value in the currency of the original record.
* **Credit \(Source\)**: the credit value in the currency of the original record.
* **Recipient**: the debtor or creditor associated with that line of the transaction.  For example, if the transaction represents a Patient Invoice the recipient column will associate the the Patient who the enterprise is billing \(this patient will be modelled as a Debtor to the enterprise in the system\).
* **Reference**: the reference points to the record column of another record/transaction that the line is linking.  An example of this is a cash payment against an invoice.  In the invoice transaction, the reference will be blank.  In the cash payment's transaction,  the line crediting the debtor's account will contain the invoice's record identifier in the "reference" column.
* **Transaction Type \[shared\]**: identifies the type of transaction.  See [Transaction Types](#transaction-types) below.
* **Responsible \[shared\]**: the user who created the transaction.
* **Comment**: this column exists only for dynamic analysis.  A user can write anything in this column and then later filter by comments to get custom totals and groups of transactions.

## Linked Transactions

Financial operations do not often occur in isolation, but are motivated by previous engagements or anticipate future operations.  For example, the expectation when a client incurs debt is that they will eventually pay off their debt, or when stock is purchased that a subsequent delivery will increase the value and quantity of the stock in a warehouse.

To reflect this real-world property, transactions in BHIMA are _linked_ by their **record** and **reference** columns.  As discussed above, the record column is the identifier for the underlying cash payment, invoice or voucher.  The reference column, however, points to the record column of another transaction somewhere in the Journal or General Ledger.  This links the two transactions, with the interpretation of that particular line in the second transaction having been motivated by the referenced first transaction.

The concept of linked transactions is best demonstrated by an example.  Below are two simplified transactions, the latter linking the former.

| **Transaction** | Record | **Account** | Debit | Credit | Entity | Reference |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| TRANS1 | IV.TPA.1 | 410001 | $10.00 |  | PA.HEV.1 |  |
| TRANS1 | IV.TPA.1 | 760001 |  | $2.50 |  |  |
| TRANS1 | IV.TPA1 | 760002 |  | $7.50 |  |  |

| **Transaction** | Record | **Account** | Debit | Credit | Entity | Reference |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| TRANS2 | CP.TPA.1 | 560001 | $4.50 |  |  |  |
| TRANS2 | CP.TPA.1 | 410001 |  | $4.50 | PA.HEV.1 | IV.TPA.1 |

The first transaction is an invoice \(denoted by `IV.TPA.1`\) for a patient \(denoted by `PA.HEV.1`\) with a total value of $10.00.  The second transaction is a cash payment \(denoted by `CP.TPA.1`\) by the same patient \(`PA.HEV.1`\) towards the previous invoice transaction \(`IV.TPA.1`\) of $4.50.

### Analysis with Linked Transactions

Since BHIMA links transactions in this way, we can perform the following analyses:

1. What is the balance of patient `PA.HEV.1`'s account after these operations?

We can take the lines that have `PA.HEV.1` as the **Entity** and sum their values together as follows:

| Transaction | Record | **Account** | Debit | Credit | Entity | Reference |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| TRANS1 | IV.TPA.1 | 410001 | $10.00 |  | PA.HEV.1 |  |
| TRANS2 | CP.TPA.1 | 410001 |  | $4.50 | PA.HEV.1 | IV.TPA.1 |
|  |  |  | **$10.00** | **$4.50** |  | - |

The balance of `PA.HEV.1`'s account is **$10.00 - $4.50** **= $5.50**.  Since the sign is positive, we say that `PA.HEV.1` has a debtor balance.

1. What is the balance of the invoice `IV.TPA.1`?

This time, we gather the invoice via its _record _`IV.TPA.1`, and all associated transactions via their _reference_ `IV.TPA.1`as shown below:

| Transaction | Record | **Account** | Debit | Credit | Entity | Reference |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| TRANS1 | IV.TPA.1 | 410001 | $10.00 |  | PA.HEV.1 |  |
| TRANS2 | CP.TPA.1 | 410001 |  | $4.50 | PA.HEV.1 | IV.TPA.1 |
|  |  |  | **$10.00** | **$4.50** |  | - |

No surprise, the balance of the invoice `IV.TPA.1` is **$10.00 - $4.50 = $5.50**.

## Transaction States

A transaction is in one of two states: _unposted_ and _posted_.  _Unposted_ transactions can be edited and deleted while _posted_ transactions are unalterable.  All transactions begin in the _unposted_ state, no matter their origin.  This indicates that they have not been validated by an accountant, and will remain in this state until an accountant posts them to the General Ledger.

Unposted and posted transactions are indicated with a light blue and a light orange dot, respectively.

Transactions are posted to the General Ledger by following these steps:

1. The transactions are audited and edited as necessary in the Journal.
2. The accountant selects one or many transactions to run a [Trial Balance](#trial-balance).
3. The Trial Balance displays the affect of the transactions on the balances of the accounts.  If any errors are caught by the application, or if the accountant observes incoherent transactions, they may return to step \(1\).
4. Once a clean Trial Balance is produced, the accountant submits the Trial Balance, posting the transactions to the General Ledger.
5. If the Journal is in its default configuration, the transactions will be filtered out of the view, indicating they have been posted to the GL.

In both states, transactions can modified by [editing a transaction](./editing-transactions.md).

## Transaction Types

Every transaction in the system has a transaction type.  Transaction types facilitate later analysis by labeling each transaction with a descriptive tag.  These are broadly grouped into the following types:

* **Income**
* **Expense**
* **Custom**

A transaction can only have a single transaction type.  You can add your own transaction types through the Transaction Type module.


## Showing Full Transactions

Because BHIMA is a double-entry accounting system, sometimes you need to find the opposite side of a transaction whilst only knowing one side.  For example, you may want to know accounts have interacted with the cash account, or find the other side of a transaction by the debit or credit value.  BHIMA facilitates these lookups in the usual way via the search modal.

In the search modal, put any identifying information of the transaction (or group of transactions) sought, then enable the "show full transactions" option in the default filters.  This will ensure the entire transaction is returned, no matter which rows are matched by the search criteria.  This may result in the `limit` property not applying, since it is being explicitly overridden to show the entirety of the transaction.