package net.thechance.wallet.exception

sealed class WalletException(message: String) : Exception(message)

class NoTransactionsFoundException(message: String): WalletException(message)