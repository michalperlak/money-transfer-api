package pl.michalperlak.moneytransfer.conf

import pl.michalperlak.moneytransfer.app.AccountsService
import pl.michalperlak.moneytransfer.repo.AccountsRepository
import pl.michalperlak.moneytransfer.repo.InMemoryAccountsRepository

class AppConfig(
        accountsRepository: AccountsRepository = InMemoryAccountsRepository(),
        val accountsService: AccountsService = AccountsService(accountsRepository)
)