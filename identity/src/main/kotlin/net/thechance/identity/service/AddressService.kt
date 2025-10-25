package net.thechance.identity.service

import jakarta.transaction.Transactional
import net.thechance.identity.entity.Address
import net.thechance.identity.exception.*
import net.thechance.identity.repository.AddressRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import net.thechance.identity.service.model.Address as AddressModel

@Service
class AddressService(
    private val addressRepository: AddressRepository,
) {

    @Transactional
    fun addAddress(userId: UUID, addressToAdd: AddressModel): Address {
        val isThereAnAddress = addressRepository.existsByUserId(userId)
        return addressRepository.save(createAddress(userId, addressToAdd, !isThereAnAddress))
    }

    @Transactional
    fun updateAddressById(addressId: UUID, userId: UUID, addressToUpdate: AddressModel): Address {
        throwIfAllValuesNull(addressToUpdate)
        val existingAddress = getAddressByIdAndUserIdOrThrow(addressId, userId)
        throwIfNeedToDisableActiveAddress(addressToUpdate, existingAddress)
        disableCurrentActiveAddressIfNeedAnotherToBeActive(addressToUpdate, existingAddress, userId)
        val updatedAddress = getUpdatedAddress(existingAddress, addressToUpdate)
        if (existingAddress == updatedAddress) {
            return existingAddress
        }
        return addressRepository.save(updatedAddress.copy(updatedAt = Instant.now()))
    }

    fun getAllAddresses(userId: UUID): List<Address> {
        return addressRepository.findByUserIdOrderByCreatedAtAsc(userId)
    }

    @Transactional
    fun deleteAddressById(addressId: UUID, userId: UUID) {
        val isActiveAddress = addressRepository.existsByIdAndUserIdAndIsActive(addressId, userId, true)
        if (isActiveAddress) throw AddressCanNotBeDeletedException()
        addressRepository.deleteById(addressId)
    }

    fun getAddressById(addressId: UUID, userId: UUID): Address {
        return addressRepository.findByIdAndUserId(addressId, userId) ?: throw AddressNotFoundException()
    }

    private fun disableCurrentActiveAddressIfNeedAnotherToBeActive(
        addressToUpdate: AddressModel,
        existingAddress: Address,
        userId: UUID,
    ) {
        if (addressToUpdate.isActive == true && !existingAddress.isActive) {
            addressRepository.deactivateActiveAddressForUser(userId)
        }
    }

    private fun throwIfNeedToDisableActiveAddress(
        addressToUpdate: AddressModel,
        existingAddress: Address,
    ) {
        if (addressToUpdate.isActive == false && existingAddress.isActive) {
            throw AddressCanNotBeUpdatedException()
        }
    }

    private fun getAddressByIdAndUserIdOrThrow(addressId: UUID, userId: UUID) =
        addressRepository.findByIdAndUserId(addressId, userId) ?: throw AddressNotFoundException()

    private fun throwIfAllValuesNull(addressToUpdate: AddressModel) {
        if (addressToUpdate.latitude == null && addressToUpdate.longitude == null && addressToUpdate.addressLine == null
            && addressToUpdate.addressType == null && addressToUpdate.isActive == null
        ) throw AtLeastAddressValueNeededException()
    }

    private fun createAddress(userId: UUID, addressToAdd: AddressModel, isActive: Boolean): Address {
        return Address(
            userId = userId,
            latitude = addressToAdd.latitude ?: throw DataNotValidException(),
            longitude = addressToAdd.longitude ?: throw DataNotValidException(),
            addressLine = addressToAdd.addressLine ?: throw DataNotValidException(),
            addressType = addressToAdd.addressType ?: throw DataNotValidException(),
            isActive = isActive
        )
    }

    private fun getUpdatedAddress(
        existingAddress: Address,
        addressToUpdate: AddressModel,
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
            isActive = isActive
        )
    }
}