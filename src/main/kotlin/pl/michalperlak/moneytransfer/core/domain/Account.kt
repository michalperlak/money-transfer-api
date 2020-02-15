package pl.michalperlak.moneytransfer.core.domain

import java.util.concurrent.atomic.AtomicReference

class Account(
        val id: AccountId,
        val currency: Currency,
        val ownerId: OwnerId
) {
    private val balanceRef: AtomicReference<Money> = AtomicReference(Money.ZERO)

    val balance: Money
        get() = balanceRef.get()

    fun deposit(amount: Money) {
        while (true) {
            val currentBalance = balanceRef.get()
            val newBalance = currentBalance + amount
            if (balanceRef.compareAndSet(currentBalance, newBalance)) {
                break
            }
        }
    }
}