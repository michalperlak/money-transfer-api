package pl.michalperlak.moneytransfer.conf

import pl.michalperlak.moneytransfer.app.AccountsService
import pl.michalperlak.moneytransfer.app.TransactionsService
import pl.michalperlak.moneytransfer.repo.AccountsRepository
import pl.michalperlak.moneytransfer.repo.InMemoryAccountsRepository
import pl.michalperlak.moneytransfer.repo.InMemoryTransactionsRepository
import pl.michalperlak.moneytransfer.repo.TransactionsRepository

class AppConfig(
        accountsRepository: AccountsRepository = InMemoryAccountsRepository(),
        transactionsRepository: TransactionsRepository = InMemoryTransactionsRepository(),
        val accountsService: AccountsService = AccountsService(accountsRepository),
        val transactionsService: TransactionsService = TransactionsService(accountsRepository, transactionsRepository)
)