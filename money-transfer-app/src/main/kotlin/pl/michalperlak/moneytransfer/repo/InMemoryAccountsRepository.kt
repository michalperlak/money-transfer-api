package pl.michalperlak.moneytransfer.repo

import pl.michalperlak.moneytransfer.core.domain.Account
import pl.michalperlak.moneytransfer.core.domain.AccountId
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class InMemoryAccountsRepository : AccountsRepository {
    private val accounts: ConcurrentMap<AccountId, Account> = ConcurrentHashMap()

    override fun saveAccount(account: Account): Mono<Account> {
        accounts[account.id] = account
        return Mono.just(account)
    }

    override fun getAccount(accountId: AccountId): Mono<Account> = Mono.justOrEmpty(accounts[accountId])
}