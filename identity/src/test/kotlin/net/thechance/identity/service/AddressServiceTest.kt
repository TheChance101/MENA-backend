package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.thechance.identity.api.dto.CreateAddressRequest
import net.thechance.identity.api.dto.UpdateAddressRequest
import net.thechance.identity.entity.Address
import net.thechance.identity.entity.User
import net.thechance.identity.entity.copy
import net.thechance.identity.exception.*
import net.thechance.identity.repository.AddressRepository
import net.thechance.identity.utils.createUser
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

class AddressServiceTest {
    private val addressRepository: AddressRepository = mockk(relaxed = true)
    private val userService: UserService = mockk(relaxed = true)
    private val addressService: AddressService =
        AddressService(addressRepository = addressRepository, userService = userService)

    //region Add Address
    @Test
    fun `addAddress() should return saved address when user exists and address is valid`() {
        every { userService.findById(dummyUserId) } returns dummyUser
        every { addressRepository.save(any()) } returnsArgument 0

        val result = addressService.addAddress(dummyUserId, createAddressRequest)

        assertThat(result.addressLine).isEqualTo("123 Main St")
        assertThat(result.user).isEqualTo(dummyUser)
        verify(exactly = 1) { addressRepository.save(any()) }
    }

    @Test
    fun `addAddress() should throw UserNotFoundException when user does not exist`() {
        every { userService.findById(dummyUserId) } throws UserNotFoundException("")

        assertThrows(UserNotFoundException::class.java) {
            addressService.addAddress(dummyUserId, createAddressRequest)
        }
        verify(exactly = 0) { addressRepository.save(any()) }
    }

    @Test
    fun `addAddress() should throw AddressNotAddedException when repository fails to save`() {
        every { userService.findById(dummyUserId) } returns dummyUser
        every { addressRepository.save(any()) } throws RuntimeException("Database error")

        assertThrows(AddressNotAddedException::class.java) {
            addressService.addAddress(dummyUserId, createAddressRequest)
        }
    }
    //endregion

    //region Update Address
    @Test
    fun `updateAddressById() should return updated address when address exists and new values are provided`() {
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns dummyAddress
        every { addressRepository.save(any()) } returnsArgument 0

        val result = addressService.updateAddressById(dummyAddressId, dummyUserId, newUpdateAddressRequest)

        assertThat(result.addressLine).isEqualTo("456 New Ave")
        assertThat(result.addressType).isEqualTo("Work")
        assertThat(result.isActive).isTrue()
        verify(exactly = 1) { addressRepository.save(any()) }
    }

    @Test
    fun `updateAddressById() should return existing address without saving when no new values are provided`() {
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns dummyAddress

        val result = addressService.updateAddressById(dummyAddressId, dummyUserId, updateAddressRequest)

        assertThat(result).isEqualTo(dummyAddress)
        verify(exactly = 0) { addressRepository.save(any()) }
    }

    @Test
    fun `updateAddressById() should throw AddressNotFoundException when address does not exist`() {
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns null

        assertThrows(AddressNotFoundException::class.java) {
            addressService.updateAddressById(dummyAddressId, dummyUserId, updateAddressRequest)
        }
    }

    @Test
    fun `updateAddressById() should throw AddressNotUpdatedException when repository fails to save`() {
        val updateRequest = updateAddressRequest.copy(latitude = 1.0)
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns dummyAddress
        every { addressRepository.save(any()) } throws RuntimeException("Database error")

        assertThrows(AddressNotUpdatedException::class.java) {
            addressService.updateAddressById(dummyAddressId, dummyUserId, updateRequest)
        }
    }

    @Test
    fun `updateAddressById() should throw AtLeastAddressValueNeededException when all values are null`() {
        assertThrows(AtLeastAddressValueNeededException::class.java) {
            addressService.updateAddressById(dummyAddressId, dummyUserId, updateAddressRequestWithNullValues)
        }
    }

    @Test
    fun `updateAddressById() should only update non-null fields when request has mixed null and non-null values`() {
        val partialUpdateRequest = updateAddressRequestWithNullValues.copy(latitude = 9.9)
        val addressSlot = slot<Address>()

        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns dummyAddress
        every { addressRepository.save(capture(addressSlot)) } returnsArgument 0

        addressService.updateAddressById(dummyAddressId, dummyUserId, partialUpdateRequest)

        val capturedAddress = addressSlot.captured
        assertThat(capturedAddress.latitude).isEqualTo(9.9)
        assertThat(capturedAddress.longitude).isEqualTo(dummyAddress.longitude)
    }

    @ParameterizedTest(name = "[{index}] {0} -> should result in {2} save calls")
    @MethodSource("provideUpdateScenarios")
    fun `updateAddressById should only save when new values are provided`(
        description: String,
        updateRequest: UpdateAddressRequest,
        expectedSaveCalls: Int
    ) {
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns dummyAddress
        every { addressRepository.save(any()) } returnsArgument 0

        addressService.updateAddressById(dummyAddressId, dummyUserId, updateRequest)

        verify(exactly = expectedSaveCalls) { addressRepository.save(any()) }
    }

    @ParameterizedTest(name = "[{index}] {0} -> should throw exception: {2}")
    @MethodSource("provideNullCheckScenarios")
    fun `updateAddressById should throw exception when all fields are null`(
        description: String,
        updateRequest: UpdateAddressRequest,
        shouldThrowException: Boolean
    ) {
        every { addressRepository.findByIdAndUserId(any(), any()) } returns dummyAddress
        every { addressRepository.save(any()) } returnsArgument 0

        if (shouldThrowException) {
            assertThrows(AtLeastAddressValueNeededException::class.java) {
                addressService.updateAddressById(dummyAddressId, dummyUserId, updateRequest)
            }
        } else {
            addressService.updateAddressById(dummyAddressId, dummyUserId, updateRequest)
        }
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
    fun `deleteAddressById() should call deleteById when address exists and is not active`() {
        val nonActiveAddress = dummyAddress.copy(isActive = false)
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns nonActiveAddress
        every { addressRepository.deleteById(dummyAddressId) } returns Unit

        addressService.deleteAddressById(dummyAddressId, dummyUserId)

        verify(exactly = 1) { addressRepository.deleteById(dummyAddressId) }
    }

    @Test
    fun `deleteAddressById() should throw AddressCanNotBeDeletedException when deleted address was active`() {
        val activeAddress = dummyAddress.copy(isActive = true)
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns activeAddress

        assertThrows(AddressCanNotBeDeletedException::class.java) {
            addressService.deleteAddressById(dummyAddressId, dummyUserId)
        }
        verify(exactly = 0) { addressRepository.deleteById(any()) }
    }

    @Test
    fun `deleteAddressById() should throw AddressNotFoundException when address to delete does not exist`() {
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns null

        assertThrows(AddressNotFoundException::class.java) {
            addressService.deleteAddressById(dummyAddressId, dummyUserId)
        }
        verify(exactly = 0) { addressRepository.deleteById(any()) }
    }

    @Test
    fun `deleteAddressById() should throw AddressNotDeletedException when repository fails to delete`() {
        val nonActiveAddress = dummyAddress.copy(isActive = false)
        every { addressRepository.findByIdAndUserId(dummyAddressId, dummyUserId) } returns nonActiveAddress
        every { addressRepository.deleteById(dummyAddressId) } throws RuntimeException("Database error")

        assertThrows(AddressNotDeletedException::class.java) {
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
        private val dummyUser: User = createUser(id = dummyUserId)
        private val dummyAddress: Address = Address(
            id = dummyAddressId,
            user = dummyUser,
            latitude = 0.0,
            longitude = 0.0,
            addressLine = "123 Main St",
            addressType = "Home"
        )
        private val createAddressRequest = CreateAddressRequest(
            latitude = dummyAddress.latitude,
            longitude = dummyAddress.longitude,
            addressLine = dummyAddress.addressLine,
            addressType = dummyAddress.addressType
        )

        private val updateAddressRequest = UpdateAddressRequest(
            latitude = dummyAddress.latitude,
            longitude = dummyAddress.longitude,
            addressLine = dummyAddress.addressLine,
            addressType = dummyAddress.addressType,
            isActive = dummyAddress.isActive,
        )
        private val newUpdateAddressRequest = UpdateAddressRequest(
            latitude = 1.0,
            longitude = 1.0,
            addressLine = "456 New Ave",
            addressType = "Work",
            isActive = true
        )

        private val updateAddressRequestWithNullValues = UpdateAddressRequest(
            latitude = null,
            longitude = null,
            addressLine = null,
            addressType = null,
            isActive = null
        )

        @JvmStatic
        fun provideUpdateScenarios(): Stream<Arguments> {

            return Stream.of(
                Arguments.of("No new values", updateAddressRequest, 0),

                Arguments.of("Latitude changed", updateAddressRequest.copy(latitude = 0.1), 1),
                Arguments.of("Longitude changed", updateAddressRequest.copy(longitude = 0.1), 1),
                Arguments.of("Address line changed", updateAddressRequest.copy(addressLine = "New Address"), 1),
                Arguments.of("Address type changed", updateAddressRequest.copy(addressType = "Work"), 1),
                Arguments.of("IsActive changed", updateAddressRequest.copy(isActive = true), 1)
            )
        }

        @JvmStatic
        fun provideNullCheckScenarios(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "All fields are null",
                    updateAddressRequestWithNullValues,
                    true
                ),

                Arguments.of(
                    "Only latitude is not null",
                    updateAddressRequestWithNullValues.copy(latitude = 1.0),
                    false
                ),
                Arguments.of(
                    "Only longitude is not null",
                    updateAddressRequestWithNullValues.copy(longitude = 1.0),
                    false
                ),
                Arguments.of(
                    "Only addressLine is not null",
                    updateAddressRequestWithNullValues.copy(addressLine = "New Address"),
                    false
                ),
                Arguments.of(
                    "Only addressType is not null",
                    updateAddressRequestWithNullValues.copy(addressType = "Work"),
                    false
                ),
                Arguments.of(
                    "Only isActive is not null",
                    updateAddressRequestWithNullValues.copy(isActive = true),
                    false
                )
            )
        }
    }
}