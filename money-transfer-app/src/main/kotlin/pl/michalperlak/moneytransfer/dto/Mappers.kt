package pl.michalperlak.moneytransfer.dto

import pl.michalperlak.moneytransfer.core.domain.Account
import pl.michalperlak.moneytransfer.core.domain.AccountId
import pl.michalperlak.moneytransfer.core.domain.OwnerId

fun Account.toDto(): AccountDto = AccountDto(
        id = id.toString(),
        currency = currency,
        ownerId = ownerId.toString(),
        balance = balance.asString()
)

fun NewAccountDto.toEntity(id: AccountId, ownerId: OwnerId): Account =
        Account(id = id, currency = currency, ownerId = ownerId)