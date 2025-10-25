package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.thechance.identity.entity.Address
import net.thechance.identity.exception.AddressCanNotBeDeletedException
import net.thechance.identity.exception.AddressNotFoundException
import net.thechance.identity.exception.AtLeastAddressValueNeededException
import net.thechance.identity.repository.AddressRepository
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.Arguments
import java.util.*
import java.util.stream.Stream
import net.thechance.identity.service.model.Address as AddressModel

class AddressServiceTest {
    private val addressRepository: AddressRepository = mockk(relaxed = true)
    private val addressService: AddressService =
        AddressService(addressRepository = addressRepository)

    //region Add Address
    @Test
    fun `addAddress() should return saved address when address is valid`() {
        every { addressRepository.save(any()) } returnsArgument 0

        val result = addressService.addAddress(dummyUserId, addressModelToCreate)

        assertThat(result.addressLine).isEqualTo("123 Main St")
        verify(exactly = 1) { addressRepository.save(any()) }
    }

    @Test
    fun `addAddress() should throw Exception when repository fails to save`() {
        every { addressRepository.save(any()) } throws RuntimeException("Database error")

        assertThrows(Exception::class.java) {
            addressService.addAddress(dummyUserId, addressModelToCreate)
        }
    }
    //endregion

    //region Update Address
    @Test
    fun `updateAddressById() should return updated address when address exists and new values are provided`() {
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns dummyAddress
        every { addressRepository.save(any()) } returnsArgument 0

        val result =
            addressService.updateAddressById(dummyAddressId, dummyUserId, newAddressToUpdate)

        assertThat(result.addressLine).isEqualTo("456 New Ave")
        assertThat(result.addressType).isEqualTo("Work")
        assertThat(result.isActive).isTrue()
        verify(exactly = 1) { addressRepository.save(any()) }
    }

    @Test
    fun `updateAddressById() should return existing address without saving when no new values are provided`() {
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns dummyAddress

        val result =
            addressService.updateAddressById(dummyAddressId, dummyUserId, addressToUpdate)

        assertThat(result).isEqualTo(dummyAddress)
        verify(exactly = 0) { addressRepository.save(any()) }
    }

    @Test
    fun `updateAddressById() should throw AddressNotFoundException when address does not exist`() {
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns null

        assertThrows(AddressNotFoundException::class.java) {
            addressService.updateAddressById(dummyAddressId, dummyUserId, addressToUpdate)
        }
    }

    @Test
    fun `updateAddressById() should throw Exception when repository fails to save`() {
        val updateRequest = addressToUpdate.copy(latitude = 1.0)
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns dummyAddress
        every { addressRepository.save(any()) } throws RuntimeException("Database error")

        assertThrows(Exception::class.java) {
            addressService.updateAddressById(dummyAddressId, dummyUserId, updateRequest)
        }
    }

    @Test
    fun `updateAddressById() should throw AtLeastAddressValueNeededException when all values are null`() {
        assertThrows(AtLeastAddressValueNeededException::class.java) {
            addressService.updateAddressById(
                dummyAddressId,
                dummyUserId,
                addressToUpdateWithNullValues
            )
        }
    }

    @Test
    fun `updateAddressById() should only update non-null fields when request has mixed null and non-null values`() {
        val partialUpdateRequest = addressToUpdateWithNullValues.copy(latitude = 9.9)
        val addressSlot = slot<Address>()

        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns dummyAddress
        every { addressRepository.save(capture(addressSlot)) } returnsArgument 0

        addressService.updateAddressById(dummyAddressId, dummyUserId, partialUpdateRequest)

        val capturedAddress = addressSlot.captured
        assertThat(capturedAddress.latitude).isEqualTo(9.9)
        assertThat(capturedAddress.longitude).isEqualTo(dummyAddress.longitude)
    }

    //endregion

    //region Get All Addresses
    @Test
    fun `getAllAddresses() should return list of addresses when user has addresses`() {
        val addresses = listOf(dummyAddress, dummyAddress.copy(id = UUID.randomUUID()))
        every { addressRepository.findByUserIdOrderByCreatedAtAsc(dummyUserId) } returns addresses

        val result = addressService.getAllAddresses(dummyUserId)

        assertThat(result).hasSize(2)
        assertThat(result).containsExactlyElementsIn(addresses)
    }

    @Test
    fun `getAllAddresses() should return empty list when user has no addresses`() {
        every { addressRepository.findByUserIdOrderByCreatedAtAsc(dummyUserId) } returns emptyList()

        val result = addressService.getAllAddresses(dummyUserId)

        assertThat(result).isEmpty()
    }
    //endregion

    //region Delete Address
    @Test
    fun `deleteAddressById() should call deleteById when address is not active`() {
        every { addressRepository.existsByIdAndUserIdAndIsActive(any(), any(), any()) } returns false
        every { addressRepository.deleteById(dummyAddressId) } returns Unit

        addressService.deleteAddressById(dummyAddressId, dummyUserId)

        verify(exactly = 1) { addressRepository.deleteById(dummyAddressId) }
    }

    @Test
    fun `deleteAddressById() should throw AddressCanNotBeDeletedException when deleted address was active`() {
        every { addressRepository.existsByIdAndUserIdAndIsActive(any(), any(), any()) } returns true

        assertThrows(AddressCanNotBeDeletedException::class.java) {
            addressService.deleteAddressById(dummyAddressId, dummyUserId)
        }
        verify(exactly = 0) { addressRepository.deleteById(any()) }
    }

    @Test
    fun `deleteAddressById() should throw Exception when repository fails to delete`() {
        val nonActiveAddress = dummyAddress.copy(isActive = false)
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns nonActiveAddress
        every { addressRepository.deleteById(dummyAddressId) } throws RuntimeException("Database error")

        assertThrows(Exception::class.java) {
            addressService.deleteAddressById(dummyAddressId, dummyUserId)
        }
    }

    //endregion

    //region Get Address By Id
    @Test
    fun `getAddressById() should return address when it exists for the user`() {
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns dummyAddress

        val result = addressService.getAddressById(dummyAddressId, dummyUserId)

        assertThat(result).isEqualTo(dummyAddress)
    }

    @Test
    fun `getAddressById() should throw AddressNotFoundException when address does not exist for the user`() {
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns null

        assertThrows(AddressNotFoundException::class.java) {
            addressService.getAddressById(dummyAddressId, dummyUserId)
        }
    }
    //endregion

    companion object {

        private val dummyAddressId: UUID = UUID.fromString("1b3ed35d-94b7-45e4-974c-9da921a27d1c")
        private val dummyUserId: UUID = UUID.fromString("a7b49002-9691-4f53-a371-379753908d9a")
        private val dummyAddress: Address = Address(
            id = dummyAddressId,
            userId = dummyUserId,
            latitude = 0.0,
            longitude = 0.0,
            addressLine = "123 Main St",
            addressType = "Home"
        )
        private val addressModelToCreate = AddressModel(
            latitude = dummyAddress.latitude,
            longitude = dummyAddress.longitude,
            addressLine = dummyAddress.addressLine,
            addressType = dummyAddress.addressType,
            isActive = false
        )

        private val addressToUpdate = AddressModel(
            latitude = dummyAddress.latitude,
            longitude = dummyAddress.longitude,
            addressLine = dummyAddress.addressLine,
            addressType = dummyAddress.addressType,
            isActive = dummyAddress.isActive,
        )
        private val newAddressToUpdate = AddressModel(
            latitude = 1.0,
            longitude = 1.0,
            addressLine = "456 New Ave",
            addressType = "Work",
            isActive = true
        )

        private val addressToUpdateWithNullValues = AddressModel(
            latitude = null,
            longitude = null,
            addressLine = null,
            addressType = null,
            isActive = null
        )
    }
}