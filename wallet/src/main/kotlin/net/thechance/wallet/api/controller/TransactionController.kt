package net.thechance.wallet.api.controller

import jakarta.servlet.http.HttpServletResponse
import net.thechance.wallet.api.controller.util.StatementMetadata
import net.thechance.wallet.api.controller.util.StatementPdfWriter
import net.thechance.wallet.api.dto.PageResponse
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
import java.time.LocalDateTime
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
        @RequestParam(name = "from", required = false) startDateTime: LocalDateTime?,
        @RequestParam(name = "to", required = false) endDateTime: LocalDateTime?,
        pageable: Pageable
    ): ResponseEntity<PageResponse<TransactionResponse>> {

        val response = transactionService.getFilteredTransactions(
            transactionFilterParams = TransactionFilterParams(types, status, startDateTime, endDateTime),
            pageable = PageRequest.of(
                pageable.pageNumber,
                pageable.pageSize,
                Sort.by(Sort.Direction.DESC, Transaction::createdAt.name)
            ),
            currentUserId = userId
        ).toResponsePage(userId)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/first-date")
    fun getUserFirstTransactionDate(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<FirstTransactionDateResponse> {
        val response = transactionService.getUserFirstTransactionDate(currentUserId = userId)
            .toFirstTransactionDateResponse()

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{transactionId}")
    fun getTransactionDetails(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable transactionId: UUID
    ): ResponseEntity<TransactionResponse> {
        val response = transactionService.getTransactionDetails(transactionId).toResponse(userId)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/statement")
    fun generateStatement(
        response: HttpServletResponse,
        @AuthenticationPrincipal userId: UUID,
        @RequestParam(name = "type", required = false) types: List<UserTransactionType>?,
        @RequestParam(name = "from", required = false) startDateTime: LocalDateTime?,
        @RequestParam(name = "to", required = false) endDateTime: LocalDateTime?,
    ) {
        val buffer = ByteArrayOutputStream()

        val metadata = statementPdfWriter.writePdfToStream(userId, types, startDateTime, endDateTime, outputStream = buffer)

        response.contentType = "application/pdf"
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=\"statement_${LocalDateTime.now()}.pdf\""
        )

        setStatementMetadataHeaders(response, metadata)

        buffer.writeTo(response.outputStream)
    }

    @PostMapping("/p2p/initiate")
    fun initiateTransaction(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody params: InitiateTransactionRequest,
    ): ResponseEntity<UUID> {
        val transaction = transactionService.initiateTransaction(
            initiateTransactionParams = params.toInitiateTransactionParam(userId, Transaction.Type.P2P)
        )

        return ResponseEntity.ok(transaction.id)
    }

    @GetMapping("/{transactionId}/receiver-details")
    fun getReceiverDetails(
        @PathVariable transactionId: UUID,
    ): ResponseEntity<ReceiverDetails> {
        val response = transactionService.getTransactionDetails(transactionId).let{
            it.receiver.toReceiverDetails(it.type)
        }

        return ResponseEntity.ok(response)
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
}