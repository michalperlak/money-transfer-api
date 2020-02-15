package pl.michalperlak.moneytransfer.repo

import pl.michalperlak.moneytransfer.core.domain.Account
import pl.michalperlak.moneytransfer.core.domain.AccountId
import reactor.core.publisher.Mono

interface AccountsRepository {
    fun saveAccount(account: Account): Mono<Account>
    fun getAccount(accountId: AccountId): Mono<Account>
}