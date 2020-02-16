package pl.michalperlak.moneytransfer.app

import arrow.core.Either
import pl.michalperlak.moneytransfer.core.domain.Account
import pl.michalperlak.moneytransfer.core.domain.AccountId
import pl.michalperlak.moneytransfer.core.domain.Money
import pl.michalperlak.moneytransfer.core.domain.Transaction
import pl.michalperlak.moneytransfer.dto.NewTransactionDto
import pl.michalperlak.moneytransfer.dto.TransactionDto
import pl.michalperlak.moneytransfer.dto.TransactionType.DEPOSIT
import pl.michalperlak.moneytransfer.dto.TransactionType.TRANSFER
import pl.michalperlak.moneytransfer.dto.toDto
import pl.michalperlak.moneytransfer.dto.translate
import pl.michalperlak.moneytransfer.error.DepositError
import pl.michalperlak.moneytransfer.error.TransactionError
import pl.michalperlak.moneytransfer.error.TransferError
import pl.michalperlak.moneytransfer.repo.AccountsRepository
import pl.michalperlak.moneytransfer.repo.TransactionsRepository
import pl.michalperlak.moneytransfer.util.errorValue
import pl.michalperlak.moneytransfer.util.extractMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class TransactionsService(
        private val accountsRepository: AccountsRepository,
        private val transactionsRepository: TransactionsRepository
) {
    fun execute(transaction: NewTransactionDto): Mono<Either<TransactionError, TransactionDto>> =
            when (transaction.type) {
                DEPOSIT -> executeDeposit(transaction)
                TRANSFER -> executeTransfer(transaction)
            }

    private fun executeDeposit(transaction: NewTransactionDto) =
            Money
                    .of(transaction.amount)
                    .mapLeft { DepositError.INVALID_AMOUNT }
                    .toMono()
                    .flatMap { deposit(it, transaction) }

    private fun deposit(amount: Either<TransactionError, Money>,
                        transaction: NewTransactionDto) =
            when (val result = amount.map { deposit(it, transaction) }) {
                is Either.Left -> Either.left(result.a).toMono()
                is Either.Right -> result.b
            }

    private fun deposit(amount: Money, transaction: NewTransactionDto) =
            getAccount(transaction.destinationAccountId, DepositError.INVALID_DESTINATION_ACCOUNT)
                    .flatMap { deposit(amount, it) }

    private fun deposit(amount: Money, account: Either<TransactionError, Account>) =
            account
                    .map { it.deposit(amount) }
                    .map {
                        transactionsRepository
                                .saveTransaction(it)
                                .map { transaction -> transaction.toDto() }
                    }
                    .extractMono()

    private fun executeTransfer(transaction: NewTransactionDto) =
            Money
                    .of(transaction.amount)
                    .mapLeft { TransferError.INVALID_AMOUNT }
                    .toMono()
                    .flatMap { transfer(it, transaction) }

    private fun transfer(amount: Either<TransactionError, Money>,
                         transaction: NewTransactionDto) =
            when (val result = amount.map { transfer(it, transaction) }) {
                is Either.Left -> Either.left(result.a).toMono()
                is Either.Right -> result.b
            }

    private fun transfer(amount: Money, transaction: NewTransactionDto) =
            getAccount(transaction.sourceAccountId, TransferError.INVALID_SOURCE_ACCOUNT)
                    .flatMap { transfer(amount, it, transaction) }

    private fun transfer(amount: Money, sourceAccount: Either<TransactionError, Account>, transaction: NewTransactionDto) =
            sourceAccount
                    .toMono()
                    .flatMap {
                        when (it) {
                            is Either.Left -> Either.left(it.a).toMono()
                            is Either.Right -> transfer(amount, it.b, transaction)
                        }
                    }

    private fun transfer(amount: Money, sourceAccount: Account, transaction: NewTransactionDto) =
            getAccount(transaction.destinationAccountId, TransferError.INVALID_DEST_ACCOUNT)
                    .flatMap {
                        when (it) {
                            is Either.Left -> it.toMono()
                            is Either.Right -> transfer(amount, sourceAccount, it.b)
                        }
                    }

    private fun transfer(amount: Money, sourceAccount: Account, destAccount: Account): Mono<Either<TransactionError, TransactionDto>> =
            sourceAccount.transfer(destAccount, amount)
                    .map { transactionsRepository.saveTransaction(it).map(Transaction::toDto) }
                    .mapLeft { it.translate() }
                    .extractMono()

    private fun getAccount(accountId: String?, error: TransactionError): Mono<Either<TransactionError, Account>> =
            accountId
                    ?.toMono()
                    ?.map { AccountId.of(it) }
                    ?.map {
                        it
                                .map { accountId ->
                                    accountsRepository.getAccount(accountId)
                                }
                                .mapLeft { error }
                    }
                    ?.flatMap { it.extractMono() }
                    ?.defaultIfEmpty(errorValue(error))
                    ?: Mono.just(errorValue(error))
}