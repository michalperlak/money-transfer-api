package pl.michalperlak.moneytransfer.app

import pl.michalperlak.moneytransfer.repo.InMemoryAccountsRepository
import java.util.UUID

fun validOwnerId() = UUID.randomUUID().toString()

fun createAccountsService(): AccountsService = AccountsService(InMemoryAccountsRepository())
