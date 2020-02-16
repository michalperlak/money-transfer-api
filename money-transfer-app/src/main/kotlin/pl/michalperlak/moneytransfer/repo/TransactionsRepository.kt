package pl.michalperlak.moneytransfer.repo

import pl.michalperlak.moneytransfer.core.domain.Transaction
import reactor.core.publisher.Mono

interface TransactionsRepository {
    fun saveTransaction(transaction: Transaction): Mono<Transaction>
}