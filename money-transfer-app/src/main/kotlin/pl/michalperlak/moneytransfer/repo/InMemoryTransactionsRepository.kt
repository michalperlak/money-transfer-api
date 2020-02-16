package pl.michalperlak.moneytransfer.repo

import pl.michalperlak.moneytransfer.core.domain.Transaction
import pl.michalperlak.moneytransfer.core.domain.TransactionId
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class InMemoryTransactionsRepository : TransactionsRepository {
    private val transactions: ConcurrentMap<TransactionId, Transaction> = ConcurrentHashMap()

    override fun saveTransaction(transaction: Transaction): Mono<Transaction> {
        transactions[transaction.transactionId] = transaction
        return Mono.just(transaction)
    }
}