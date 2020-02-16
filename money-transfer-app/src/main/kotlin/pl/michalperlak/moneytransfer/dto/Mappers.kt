package pl.michalperlak.moneytransfer.dto

import pl.michalperlak.moneytransfer.core.domain.Account
import pl.michalperlak.moneytransfer.core.domain.AccountId
import pl.michalperlak.moneytransfer.core.domain.Deposit
import pl.michalperlak.moneytransfer.core.domain.OwnerId
import pl.michalperlak.moneytransfer.core.domain.Transaction
import pl.michalperlak.moneytransfer.core.domain.TransactionType.DEPOSIT

fun Account.toDto(): AccountDto = AccountDto(
        id = id.toString(),
        currency = currency,
        ownerId = ownerId.toString(),
        balance = balance.asString()
)

fun NewAccountDto.toEntity(id: AccountId, ownerId: OwnerId): Account =
        Account(id = id, currency = currency, ownerId = ownerId)

fun Transaction.toDto(): TransactionDto =
        when (this) {
            is Deposit -> this.toTransactionDto()
        }

private fun Deposit.toTransactionDto(): TransactionDto = TransactionDto(
        transactionId = transactionId.toString(),
        type = DEPOSIT,
        amount = amount.asString(),
        destinationAccountId = accountId.toString()
)