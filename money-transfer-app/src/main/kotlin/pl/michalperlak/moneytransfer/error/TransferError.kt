package pl.michalperlak.moneytransfer.error

enum class TransferError(override val message: String) : TransactionError {
    INVALID_AMOUNT("Invalid amount"),
    INVALID_SOURCE_ACCOUNT("Invalid source account"),
    INVALID_DEST_ACCOUNT("Invalid destination account"),
    INCOMPATIBLE_CURRENCIES("Incompatible currencies"),
    INSUFFICIENT_FUNDS("Insufficient funds")
}