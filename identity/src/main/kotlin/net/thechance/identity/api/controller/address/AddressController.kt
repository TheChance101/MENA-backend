package net.thechance.identity.api.controller.address

import jakarta.validation.Valid
import net.thechance.identity.api.dto.address.AddressResponse
import net.thechance.identity.api.dto.address.CreateAddressRequest
import net.thechance.identity.api.dto.address.UpdateAddressRequest
import net.thechance.identity.api.mapper.toAddressModel
import net.thechance.identity.api.mapper.toResponse
import net.thechance.identity.service.AddressService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/identity/addresses")
class AddressController(
    private val addressService: AddressService
) {
    @PutMapping("/{id}")
    fun updateAddressById(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateAddressRequest
    ): ResponseEntity<AddressResponse> {
        val updateAddressResponse = addressService.updateAddressById(id, userId, request.toAddressModel()).toResponse()
        return ResponseEntity.ok(updateAddressResponse)
    }

    @PostMapping
    fun createAddress(
        @AuthenticationPrincipal userId: UUID,
        @Valid @RequestBody request: CreateAddressRequest
    ): ResponseEntity<AddressResponse> {
        val createAddressResponse = addressService.addAddress(userId, request.toAddressModel()).toResponse()
        return ResponseEntity.ok(createAddressResponse)
    }

    @GetMapping("/{id}")
    fun getAddressById(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable id: UUID
    ): ResponseEntity<AddressResponse> {
        val getAddressResponse = addressService.getAddressById(id, userId).toResponse()
        return ResponseEntity.ok(getAddressResponse)
    }

    @GetMapping
    fun getAddresses(
        @PageableDefault(page = 0, size = 20)
        pageable: Pageable,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<List<AddressResponse>> {
        val getPageableAddressesResponse =
            addressService.getPageableAddresses(userId, pageable).content.map { it.toResponse() }
        return ResponseEntity.ok(getPageableAddressesResponse)
    }

    @GetMapping("/active")
    fun getActiveAddress(
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<AddressResponse> {
        val activeAddressResponse = addressService.getActiveAddress(userId).toResponse()
        return ResponseEntity.ok(activeAddressResponse)
    }

    @DeleteMapping("/{id}")
    fun deleteAddress(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable id: UUID
    ): ResponseEntity<Unit> {
        return ResponseEntity.ok(addressService.deleteAddressById(id, userId))
    }
}