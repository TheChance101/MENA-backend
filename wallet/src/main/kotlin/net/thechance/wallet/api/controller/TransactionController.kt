package net.thechance.wallet.api.controller

import jakarta.servlet.http.HttpServletResponse
import net.thechance.wallet.api.controller.util.StatementMetadata
import net.thechance.wallet.api.controller.util.StatementPdfWriter
import net.thechance.wallet.api.dto.transaction.*
import net.thechance.wallet.entity.Transaction
import net.thechance.wallet.service.TransactionService
import net.thechance.wallet.service.model.input.TransactionFilterParams
import net.thechance.wallet.service.model.input.UserTransactionType
import net.thechance.wallet.service.model.output.ReceiverDetails
import net.thechance.wallet.service.model.output.toReceiverDetails
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@RestController
@RequestMapping("/wallet/transactions")
class TransactionController(
    private val transactionService: TransactionService,
    private val statementPdfWriter: StatementPdfWriter,
) {
    @GetMapping
    fun getFilteredTransactions(
        @AuthenticationPrincipal userId: UUID,
        @RequestParam(name = "type", required = false) types: List<UserTransactionType>?,
        @RequestParam(required = false) status: Transaction.Status?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
        pageable: Pageable
    ): ResponseEntity<TransactionPageResponse> {

        val transactions =
            transactionService.getFilteredTransactions(
                TransactionFilterParams(
                    types = types,
                    status = status,
                    startDate = startDate,
                    endDate = endDate
                ),
                pageable = PageRequest.of(
                    pageable.pageNumber,
                    pageable.pageSize,
                    Sort.by(Sort.Direction.DESC, Transaction::createdAt.name)
                ),
                currentUserId = userId
            )
                .map { it.toResponse(currentUserId = userId) }


        val response = TransactionPageResponse(
            transactions = transactions.content,
            page = transactions.number,
            pageSize = transactions.size,
            totalElements = transactions.totalElements,
            totalPages = transactions.totalPages
        )
        return ResponseEntity.ok(response)
    }

    @GetMapping("/first-date")
    fun getUserFirstTransactionDate(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<FirstTransactionDateResponse> {

        val date = transactionService.getUserFirstTransactionDate(currentUserId = userId)?.toLocalDate()
        return ResponseEntity.ok(FirstTransactionDateResponse(firstTransactionDate = date))
    }

    @GetMapping("/{transactionId}")
    fun getTransactionDetails(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable transactionId: UUID
    ): ResponseEntity<TransactionResponse> {
        val transactionDetails = transactionService.getTransactionDetails(transactionId)
        return ResponseEntity.ok(transactionDetails.toResponse(userId))
    }

    @GetMapping("/statement")
    fun generateStatement(
        response: HttpServletResponse,
        @AuthenticationPrincipal userId: UUID,
        @RequestParam(name = "type", required = false) types: List<UserTransactionType>?,
        @RequestParam(required = false) startDate: LocalDate?,
        @RequestParam(required = false) endDate: LocalDate?,
    ) {
        val buffer = ByteArrayOutputStream()

        val metadata = statementPdfWriter.writePdfToStream(
            userId = userId,
            types = types,
            startDate = startDate,
            endDate = endDate,
            outputStream = buffer
        )

        response.contentType = "application/pdf"
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=\"statement${startDate.formatDate()}_to${endDate.formatDate()}.pdf\""
        )

        setStatementMetadataHeaders(response, metadata)

        buffer.writeTo(response.outputStream)
    }

    private fun setStatementMetadataHeaders(
        response: HttpServletResponse,
        metadata: StatementMetadata
    ) {
        response.setHeader("X-Statement-Total-Inflows", metadata.totalInflows.toString())
        response.setHeader("X-Statement-Total-Outflows", metadata.totalOutflows.toString())
        response.setHeader("X-Statement-Start-Date", metadata.startDate.toString())
        response.setHeader("X-Statement-End-Date", metadata.endDate.toString())
    }

    @PostMapping("/p2p/initiate")
    fun initiateTransaction(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody params: InitiateTransactionRequest,
    ): ResponseEntity<UUID> {
        val transaction = transactionService.initiateTransaction(params.toInitiateTransactionParam(userId, Transaction.Type.P2P))
        return ResponseEntity.ok(transaction.id)
    }

    @GetMapping("/{transactionId}/receiver-details")
    fun getReceiverDetails(
        @PathVariable transactionId: UUID,
    ): ResponseEntity<ReceiverDetails> {
        val receiverDetails = transactionService.getTransactionDetails(transactionId)
        return ResponseEntity.ok(receiverDetails.receiver.toReceiverDetails(receiverDetails.type))
    }

    private fun LocalDate?.formatDate(): String =
        this?.format(DateTimeFormatter.ofPattern("_dd_MMM_yyyy"))?.lowercase() ?: ""
}