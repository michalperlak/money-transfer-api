package pl.michalperlak.moneytransfer.error

enum class DepositError(override val message: String) : TransactionError {
    INVALID_AMOUNT("Invalid amount"),
    INVALID_DESTINATION_ACCOUNT("Invalid destination account")
}