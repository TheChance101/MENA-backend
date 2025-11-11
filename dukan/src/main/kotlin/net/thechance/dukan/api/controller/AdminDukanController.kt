package net.thechance.dukan.api.controller

import net.thechance.dukan.api.dto.dukan.AdminDukanResponse
import net.thechance.dukan.api.dto.dukan.UpdateDukanStatusRequest
import net.thechance.dukan.api.mapper.dukan.toAdminResponse
import net.thechance.dukan.entity.Dukan
import net.thechance.dukan.service.DukanService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/dukan/admin")
class AdminDukanController(
    private val dukanService: DukanService
) {

    @GetMapping
    fun getDukansByStatusAndSearchQuery(
        @RequestParam(required = false) query: String = "",
        @RequestParam status: Dukan.Status,
        pageable: Pageable
    ): ResponseEntity<Page<AdminDukanResponse>> {
        val dukans = dukanService.getDukansByStatusAndQuery(
            query = query,
            status = status,
            pageable = pageable
        )

        val response = dukans.map { it.toAdminResponse() }
        return ResponseEntity.ok(response)
    }

    @PatchMapping("{dukanId}/status")
    fun updateDukanStatus(
        @PathVariable("dukanId") dukanId: UUID,
        @RequestBody updateStatusRequest: UpdateDukanStatusRequest
    ): ResponseEntity<Unit> {
        dukanService.updateDukanStatus(
            dukanId = dukanId,
            status = updateStatusRequest.status,
            reason = updateStatusRequest.reason
        )

        return ResponseEntity.ok().build()
    }
}
