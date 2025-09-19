package net.thechance.chat.service

import net.thechance.chat.entity.ContactUser
import net.thechance.chat.repository.ContactUserRepository
import org.springframework.stereotype.Service

@Service
class ContactUserService(
    private val contactUserRepository: ContactUserRepository
) {
    init { // Initialize with some contact users if the repository is empty for testing purposes
        if (contactUserRepository.count() == 0L) {
            contactUserRepository.saveAll(
                listOf(
                    ContactUser(firstName = "Alice", lastName = "Anderson", phoneNumber = "111", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "Brian", lastName = "Brown", phoneNumber = "222", imageUrl = null),
                    ContactUser(firstName = "Catherine", lastName = "Clark", phoneNumber = "333", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "David", lastName = "Davis", phoneNumber = "444", imageUrl = null),
                    ContactUser(firstName = "Emma", lastName = "Evans", phoneNumber = "555", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "Frank", lastName = "Foster", phoneNumber = "666", imageUrl = null),
                    ContactUser(firstName = "Grace", lastName = "Green", phoneNumber = "777", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "Hannah", lastName = "Hill", phoneNumber = "888", imageUrl = null),
                    ContactUser(firstName = "Ian", lastName = "Irwin", phoneNumber = "999", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "Julia", lastName = "Jones", phoneNumber = "101", imageUrl = null),
                    ContactUser(firstName = "Kevin", lastName = "King", phoneNumber = "202", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "Laura", lastName = "Lewis", phoneNumber = "303", imageUrl = null),
                    ContactUser(firstName = "Michael", lastName = "Moore", phoneNumber = "404", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "Nina", lastName = "Nelson", phoneNumber = "505", imageUrl = null),
                    ContactUser(firstName = "Oliver", lastName = "Owens", phoneNumber = "606", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "Paul", lastName = "Parker", phoneNumber = "707", imageUrl = null),
                    ContactUser(firstName = "Quinn", lastName = "Quincy", phoneNumber = "808", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "Rachel", lastName = "Reed", phoneNumber = "909", imageUrl = null),
                    ContactUser(firstName = "Sam", lastName = "Smith", phoneNumber = "112", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "Tina", lastName = "Turner", phoneNumber = "223", imageUrl = null),
                    ContactUser(firstName = "Uma", lastName = "Underwood", phoneNumber = "334", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "Victor", lastName = "Vance", phoneNumber = "445", imageUrl = null),
                    ContactUser(firstName = "Wendy", lastName = "White", phoneNumber = "556", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "Xavier", lastName = "Xanders", phoneNumber = "667", imageUrl = null),
                    ContactUser(firstName = "Yvonne", lastName = "Young", phoneNumber = "778", imageUrl = "https://picsum.photos/200"),
                    ContactUser(firstName = "Zach", lastName = "Zimmer", phoneNumber = "889", imageUrl = null)
                )
            )
        }
    }
}