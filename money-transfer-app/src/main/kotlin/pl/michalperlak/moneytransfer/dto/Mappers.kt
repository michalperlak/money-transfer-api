package pl.michalperlak.moneytransfer.dto

import pl.michalperlak.moneytransfer.core.domain.Account
import pl.michalperlak.moneytransfer.core.domain.AccountId
import pl.michalperlak.moneytransfer.core.domain.Deposit
import pl.michalperlak.moneytransfer.core.domain.OwnerId
import pl.michalperlak.moneytransfer.core.domain.Transaction
import pl.michalperlak.moneytransfer.core.domain.Transfer
import pl.michalperlak.moneytransfer.core.domain.TransferError.INCOMPATIBLE_CURRENCIES
import pl.michalperlak.moneytransfer.core.domain.TransferError.INSUFFICIENT_FUNDS
import pl.michalperlak.moneytransfer.dto.TransactionType.DEPOSIT
import pl.michalperlak.moneytransfer.dto.TransactionType.TRANSFER
import pl.michalperlak.moneytransfer.error.TransactionError
import pl.michalperlak.moneytransfer.error.TransferError
import pl.michalperlak.moneytransfer.core.domain.TransferError as TransferFailure

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
            is Deposit -> toTransactionDto()
            is Transfer -> toTransactionDto()
        }

fun TransferFailure.translate(): TransactionError =
        when (this) {
            INCOMPATIBLE_CURRENCIES -> TransferError.INCOMPATIBLE_CURRENCIES
            INSUFFICIENT_FUNDS -> TransferError.INSUFFICIENT_FUNDS
        }

private fun Deposit.toTransactionDto(): TransactionDto = TransactionDto(
        transactionId = transactionId.toString(),
        type = DEPOSIT,
        amount = amount.asString(),
        destinationAccountId = accountId.toString()
)

private fun Transfer.toTransactionDto(): TransactionDto = TransactionDto(
        transactionId = transactionId.toString(),
        type = TRANSFER,
        amount = amount.asString(),
        sourceAccountId = sourceAccountId.toString(),
        destinationAccountId = destAccountId.toString()
)