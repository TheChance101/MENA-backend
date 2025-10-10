package net.thechance.identity.service

import jakarta.transaction.Transactional
import net.thechance.identity.api.dto.CreateAddressRequest
import net.thechance.identity.api.dto.UpdateAddressRequest
import net.thechance.identity.entity.Address
import net.thechance.identity.entity.User
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
    fun addAddress(userId: UUID, addressToAdd: CreateAddressRequest): Address {
        val user = userService.findById(userId)
        return try {
            addressRepository.save(createAddress(user, addressToAdd))
        } catch (_: Exception) {
            throw AddressNotAddedException()
        }
    }

    @Transactional
    fun updateAddressById(addressId: UUID, userId: UUID, addressToUpdate: UpdateAddressRequest): Address {
        val existingAddress = addressRepository.findByIdAndUserId(addressId, userId) ?: throw AddressNotFoundException()
        if (!isThereNewValues(existingAddress, addressToUpdate)) return existingAddress
        return try {
            addressRepository.save(getUpdatedAddress(existingAddress, addressToUpdate))
        } catch (_: Exception) {
            throw AddressNotUpdatedException()
        }
    }

    fun getAllAddresses(userId: UUID): List<Address> {
        return addressRepository.findByUserIdOrderByCreatedAtAsc(userId)
    }

    @Transactional
    fun deleteAddressById(addressId: UUID, userId: UUID) {
        if (isActiveAddress(userId, addressId)) throw AddressCanNotBeDeletedException()
        try {
            addressRepository.deleteById(addressId)
        } catch (_: Exception) {
            throw AddressNotDeletedException()
        }
    }

    fun getAddressById(addressId: UUID, userId: UUID): Address {
        return addressRepository.findByIdAndUserId(addressId, userId) ?: throw AddressNotFoundException()
    }

    private fun createAddress(user: User, addressToAdd: CreateAddressRequest): Address {
        return Address(
            user = user,
            latitude = addressToAdd.latitude,
            longitude = addressToAdd.longitude,
            addressLine = addressToAdd.addressLine,
            addressType = addressToAdd.addressType,
        )
    }

    private fun isThereNewValues(existingAddress: Address, addressToUpdate: UpdateAddressRequest): Boolean {
        return existingAddress.latitude != addressToUpdate.latitude
                || existingAddress.longitude != addressToUpdate.longitude
                || existingAddress.addressLine != addressToUpdate.addressLine
                || existingAddress.addressType != addressToUpdate.addressType
                || existingAddress.isActive != addressToUpdate.isActive
    }

    private fun getUpdatedAddress(existingAddress: Address, addressToUpdate: UpdateAddressRequest): Address {
        return existingAddress.copy(
            latitude = addressToUpdate.latitude ?: existingAddress.latitude,
            longitude = addressToUpdate.longitude ?: existingAddress.longitude,
            addressLine = addressToUpdate.addressLine ?: existingAddress.addressLine,
            addressType = addressToUpdate.addressType ?: existingAddress.addressType,
            isActive = addressToUpdate.isActive ?: existingAddress.isActive,
            updatedAt = Instant.now()
        )
    }

    private fun isActiveAddress(userId: UUID, addressId: UUID): Boolean {
        return addressRepository.findByIdAndUserId(addressId, userId)?.isActive ?: throw AddressNotFoundException()
    }
}