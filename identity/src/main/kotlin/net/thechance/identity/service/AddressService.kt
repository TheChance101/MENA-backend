package net.thechance.identity.service

import jakarta.transaction.Transactional
import net.thechance.identity.api.dto.CreateAddressRequest
import net.thechance.identity.api.dto.UpdateAddressRequest
import net.thechance.identity.entity.Address
import net.thechance.identity.entity.copy
import net.thechance.identity.exception.*
import net.thechance.identity.repository.AddressRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class AddressService(
    private val addressRepository: AddressRepository,
    private val userService: UserService
) {

    @Transactional
    fun addAddress(userId: UUID, request: CreateAddressRequest): Address {
        val user = userService.findById(userId)
        val newAddress = Address(
            user = user,
            latitude = request.latitude,
            longitude = request.longitude,
            addressLine = request.addressLine,
            addressType = request.addressType,
        )
        return try {
            addressRepository.save(newAddress)
        } catch (exception: Exception) {
            throw AddressNotAddedException()
        }
    }

    @Transactional
    fun updateAddress(userId: UUID, addressId: UUID, request: UpdateAddressRequest): Address {
        val existingAddress = addressRepository.findByIdAndUserId(addressId, userId) ?: throw AddressNotFoundException()
        if (!isThereNewValues(existingAddress, request)) return existingAddress

        val updatedAddress = existingAddress.copy(
            latitude = request.latitude ?: existingAddress.latitude,
            longitude = request.longitude ?: existingAddress.longitude,
            addressLine = request.addressLine ?: existingAddress.addressLine,
            addressType = request.addressType ?: existingAddress.addressType,
            isActive = request.isActive ?: existingAddress.isActive,
            updatedAt = Instant.now()
        )

        return try {
            addressRepository.save(updatedAddress)
        } catch (exception: Exception) {
            throw AddressNotUpdatedException()
        }
    }

    fun getAllAddresses(userId: UUID): List<Address> {
        return addressRepository.findByUserIdOrderByCreatedAtAsc(userId)
    }

    @Transactional
    fun deleteAddressById(userId: UUID, addressId: UUID) {
        if (isActiveAddress(userId, addressId)) putOldestAddressToBeActive(userId)
        try {
            addressRepository.deleteById(addressId)
        } catch (_: Exception) {
            throw AddressNotDeletedException()
        }
    }

    fun getAddressById(userId: UUID, addressId: UUID): Address {
        return addressRepository.findByIdAndUserId(addressId, userId) ?: throw AddressNotFoundException()
    }

    private fun isThereNewValues(existingAddress: Address, request: UpdateAddressRequest): Boolean {
        return existingAddress.latitude != request.latitude
                || existingAddress.longitude != request.longitude
                || existingAddress.addressLine != request.addressLine
                || existingAddress.addressType != request.addressType
                || existingAddress.isActive != request.isActive
    }

    private fun isActiveAddress(userId: UUID, addressId: UUID): Boolean {
        return addressRepository.findByIdAndUserId(addressId, userId)?.isActive ?: throw AddressNotFoundException()
    }

    @Transactional
    private fun putOldestAddressToBeActive(userId: UUID) {
        val oldestAddress =
            addressRepository.findFirstByUserIdOrderByCreatedAtAsc(userId) ?: throw AddressCanNotBeDeletedException()
        val updatedAddress = oldestAddress.copy(isActive = true)
        addressRepository.save(updatedAddress)
    }
}