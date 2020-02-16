package pl.michalperlak.moneytransfer.core.domain

import arrow.core.Either
import pl.michalperlak.moneytransfer.core.domain.TransferError.INCOMPATIBLE_CURRENCIES
import pl.michalperlak.moneytransfer.core.domain.TransferError.INSUFFICIENT_FUNDS
import java.util.concurrent.atomic.AtomicReference

class Account(
        val id: AccountId,
        val currency: Currency,
        val ownerId: OwnerId
) {
    private val balanceRef: AtomicReference<Money> = AtomicReference(Money.ZERO)

    val balance: Money
        get() = balanceRef.get()

    fun deposit(amount: Money): Transaction {
        while (true) {
            val currentBalance = balanceRef.get()
            val newBalance = currentBalance + amount
            if (balanceRef.compareAndSet(currentBalance, newBalance)) {
                break
            }
        }
        return Deposit(transactionId = TransactionId.generate(), amount = amount, accountId = id)
    }

    fun transfer(to: Account, amount: Money): Either<TransferError, Money> {
        if (currency != to.currency) {
            return Either.left(INCOMPATIBLE_CURRENCIES)
        }
        while (true) {
            val myBalance = balanceRef.get()
            if (myBalance < amount) {
                return Either.left(INSUFFICIENT_FUNDS)
            }
            val updatedBalance = myBalance - amount
            if (balanceRef.compareAndSet(myBalance, updatedBalance)) {
                break
            }
        }
        val recipientBalanceRef = to.balanceRef
        while (true) {
            val recipientBalance = recipientBalanceRef.get()
            val updatedBalance = recipientBalance + amount
            if (recipientBalanceRef.compareAndSet(recipientBalance, updatedBalance)) {
                break
            }
        }
        return Either.right(balanceRef.get())
    }
}