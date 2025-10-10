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
            val address = if (addressRepository.findAll().isEmpty()) createAddress(user, addressToAdd, true)
            else createAddress(user, addressToAdd)
            addressRepository.save(address)
        } catch (_: Exception) {
            throw AddressNotAddedException()
        }
    }

    @Transactional
    fun updateAddressById(addressId: UUID, userId: UUID, addressToUpdate: UpdateAddressRequest): Address {
        if (isAllAddressValuesNull(addressToUpdate)) throw AtLeastAddressValueNeededException()
        val existingAddress = addressRepository.findByIdAndUserId(addressId, userId) ?: throw AddressNotFoundException()
        if (!isThereNewValues(existingAddress, addressToUpdate)) return existingAddress
        if (addressToUpdate.isActive == false && existingAddress.isActive) {
            throw AddressCanNotBeUpdatedException()
        }
        try {
            val updatedAddress = getUpdatedAddress(existingAddress, addressToUpdate)
            if (addressToUpdate.isActive == true && !existingAddress.isActive) {
                disableActiveAddress(userId)
            }
            return addressRepository.save(updatedAddress)
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

    private fun createAddress(user: User, addressToAdd: CreateAddressRequest, isActive: Boolean = false): Address {
        return Address(
            user = user,
            latitude = addressToAdd.latitude,
            longitude = addressToAdd.longitude,
            addressLine = addressToAdd.addressLine,
            addressType = addressToAdd.addressType,
            isActive = isActive
        )
    }

    private fun isThereNewValues(existingAddress: Address, addressToUpdate: UpdateAddressRequest): Boolean {
        return isUpdatedAndNotNull(existingAddress.latitude, addressToUpdate.latitude) ||
                isUpdatedAndNotNull(existingAddress.longitude, addressToUpdate.longitude) ||
                isUpdatedAndNotNull(existingAddress.addressLine, addressToUpdate.addressLine) ||
                isUpdatedAndNotNull(existingAddress.addressType, addressToUpdate.addressType) ||
                isUpdatedAndNotNull(existingAddress.isActive, addressToUpdate.isActive)
    }

    private fun <T> isUpdatedAndNotNull(existing: T, new: T?): Boolean {
        return new != null && existing != new
    }

    private fun getUpdatedAddress(
        existingAddress: Address,
        addressToUpdate: UpdateAddressRequest
    ): Address {
        var isActive = existingAddress.isActive
        if (addressToUpdate.isActive == true && !existingAddress.isActive) {
            isActive = addressToUpdate.isActive
        }
        return existingAddress.copy(
            latitude = addressToUpdate.latitude ?: existingAddress.latitude,
            longitude = addressToUpdate.longitude ?: existingAddress.longitude,
            addressLine = addressToUpdate.addressLine ?: existingAddress.addressLine,
            addressType = addressToUpdate.addressType ?: existingAddress.addressType,
            isActive = isActive,
            updatedAt = Instant.now()
        )
    }

    private fun isActiveAddress(userId: UUID, addressId: UUID): Boolean {
        return addressRepository.findByIdAndUserId(addressId, userId)?.isActive ?: throw AddressNotFoundException()
    }

    private fun disableActiveAddress(userId: UUID) {
        val address = addressRepository.findByIsActiveAndUserId(true, userId) ?: return
        addressRepository.save(address.copy(isActive = false))
    }

    private fun isAllAddressValuesNull(address: UpdateAddressRequest): Boolean {
        return address.latitude == null && address.longitude == null && address.addressLine == null
                && address.addressType == null && address.isActive == null
    }
}