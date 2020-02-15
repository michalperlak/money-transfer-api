package pl.michalperlak.moneytransfer.app

import arrow.core.Either
import pl.michalperlak.moneytransfer.core.domain.Account
import pl.michalperlak.moneytransfer.core.domain.AccountId
import pl.michalperlak.moneytransfer.core.domain.OwnerId
import pl.michalperlak.moneytransfer.dto.AccountDto
import pl.michalperlak.moneytransfer.dto.NewAccountDto
import pl.michalperlak.moneytransfer.dto.toDto
import pl.michalperlak.moneytransfer.dto.toEntity
import pl.michalperlak.moneytransfer.error.AccountCreationError
import pl.michalperlak.moneytransfer.repo.AccountsRepository
import reactor.core.publisher.Mono

class AccountsService(
        private val accountsRepository: AccountsRepository
) {
    fun createAccount(newAccount: NewAccountDto): Either<AccountCreationError, Mono<AccountDto>> {
        val accountId = AccountId.generate()
        return OwnerId
                .of(newAccount.ownerId)
                .map { newAccount.toEntity(accountId, it) }
                .map { saveAccount(it) }
                .mapLeft { AccountCreationError.INVALID_OWNER_ID }
    }

    private fun saveAccount(account: Account): Mono<AccountDto> =
            accountsRepository
                    .saveAccount(account)
                    .map { it.toDto() }
}