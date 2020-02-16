package pl.michalperlak.moneytransfer.core.domain

sealed class Transaction(
        val transactionId: TransactionId,
        val amount: Money
)

class Deposit(
        transactionId: TransactionId,
        amount: Money,
        val accountId: AccountId
) : Transaction(transactionId, amount)