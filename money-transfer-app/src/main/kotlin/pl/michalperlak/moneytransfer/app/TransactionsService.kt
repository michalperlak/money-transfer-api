package pl.michalperlak.moneytransfer.app

import arrow.core.Either
import arrow.core.flatMap
import pl.michalperlak.moneytransfer.core.domain.Account
import pl.michalperlak.moneytransfer.core.domain.AccountId
import pl.michalperlak.moneytransfer.core.domain.Money
import pl.michalperlak.moneytransfer.core.domain.TransactionType.DEPOSIT
import pl.michalperlak.moneytransfer.dto.NewTransactionDto
import pl.michalperlak.moneytransfer.dto.TransactionDto
import pl.michalperlak.moneytransfer.dto.toDto
import pl.michalperlak.moneytransfer.error.DepositError.INVALID_AMOUNT
import pl.michalperlak.moneytransfer.error.DepositError.INVALID_DESTINATION_ACCOUNT
import pl.michalperlak.moneytransfer.error.TransactionError
import pl.michalperlak.moneytransfer.repo.AccountsRepository
import pl.michalperlak.moneytransfer.repo.TransactionsRepository
import reactor.core.publisher.Mono

class TransactionsService(
        private val accountsRepository: AccountsRepository,
        private val transactionsRepository: TransactionsRepository
) {
    fun execute(transaction: NewTransactionDto): Either<TransactionError, Mono<TransactionDto>> =
            when (transaction.type) {
                DEPOSIT -> executeDeposit(transaction)
            }

    private fun executeDeposit(transaction: NewTransactionDto): Either<TransactionError, Mono<TransactionDto>> =
            Money
                    .of(transaction.amount)
                    .mapLeft { INVALID_AMOUNT }
                    .flatMap { deposit(it, transaction) }

    private fun deposit(amount: Money, transaction: NewTransactionDto): Either<TransactionError, Mono<TransactionDto>> {
        val account = getAccount(transaction.destinationAccountId, INVALID_DESTINATION_ACCOUNT)
        return account
                .map { deposit(amount, it) }
    }

    private fun deposit(amount: Money, account: Mono<Account>): Mono<TransactionDto> =
            account
                    .map { it.deposit(amount) }
                    .flatMap { transactionsRepository.saveTransaction(it) }
                    .map { it.toDto() }

    private fun getAccount(accountId: String?, error: TransactionError): Either<TransactionError, Mono<Account>> {
        return accountId
                ?.let { AccountId.of(it) }
                ?.map { accountsRepository.getAccount(it) }
                ?.mapLeft { error }
                ?: return Either.left(error)
    }
}