package pl.michalperlak.moneytransfer.repo

import pl.michalperlak.moneytransfer.core.domain.Account
import reactor.core.publisher.Mono

interface AccountsRepository {
    fun saveAccount(account: Account): Mono<Account>
}