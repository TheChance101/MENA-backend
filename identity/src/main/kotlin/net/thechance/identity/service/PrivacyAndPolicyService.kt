package net.thechance.identity.service

import net.thechance.identity.service.model.PrivacyAndPolicyModel
import net.thechance.identity.service.model.PrivacyAndPolicySectionModel
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class PrivacyAndPolicyService {
    fun getPrivacyAndPolicy(language: String): PrivacyAndPolicyModel {
        return PrivacyAndPolicyModel(
            updatedAt = updatedAt,
            sections = privacyAndPolicy.getValue(language),
        )
    }

    companion object {
        private val updatedAt = Instant.parse("2026-11-11T00:00:00Z")
        private val privacyAndPolicy = mapOf<String, List<PrivacyAndPolicySectionModel>>(
            "en" to List(3) {
                PrivacyAndPolicySectionModel(
                    title = "What is Lorem Ipsum?",
                    content = "is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries"
                )
            },
            "ar" to List(3) {
                PrivacyAndPolicySectionModel(
                    title = "ما هو لوريم إيبسوم؟",
                    content = "هو مجرد نص وهمي يُستخدم في صناعات الطباعة وتنسيق النصوص. لقد كان لوريم إيبسوم النص الوهمي القياسي في هذه الصناعة منذ القرن السادس عشر (منذ عام 1500 تقريباً)، عندما أخذت مطبعة مجهولة مجموعة من المحارف وقامت بخلطها بشكل عشوائي لتكوين كتاب عينات من المحارف. لقد صمد هذا النص لأكثر من خمسة قرون."
                )
            },
        )
    }
}
