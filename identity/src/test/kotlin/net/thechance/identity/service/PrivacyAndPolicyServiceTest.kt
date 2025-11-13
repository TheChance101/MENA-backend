package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import net.thechance.identity.service.model.PrivacyAndPolicySectionModel
import org.junit.Test

class PrivacyAndPolicyServiceTest {
    private val privacyAndPolicyService = PrivacyAndPolicyService()

    @Test
    fun `getPrivacyAndPolicy() should return english data when en is passed`() {
        val result = privacyAndPolicyService.getPrivacyAndPolicy(LANGUAGE_EN).sections

        assertThat(result).isEqualTo(sectionsModelEn)
    }

    @Test
    fun `getPrivacyAndPolicy() should return arabic data when ar is passed`() {
        val result = privacyAndPolicyService.getPrivacyAndPolicy(LANGUAGE_AR).sections

        assertThat(result).isEqualTo(sectionsModelAr)
    }

    companion object {
        private const val LANGUAGE_EN = "en"
        private const val LANGUAGE_AR = "ar"

        private val sectionsModelEn = List(3) {
            PrivacyAndPolicySectionModel(
                title = "What is Lorem Ipsum?",
                content = "is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries"
            )
        }

        private val sectionsModelAr = List(3) {
            PrivacyAndPolicySectionModel(
                title = "ما هو لوريم إيبسوم؟",
                content = "هو مجرد نص وهمي يُستخدم في صناعات الطباعة وتنسيق النصوص. لقد كان لوريم إيبسوم النص الوهمي القياسي في هذه الصناعة منذ القرن السادس عشر (منذ عام 1500 تقريباً)، عندما أخذت مطبعة مجهولة مجموعة من المحارف وقامت بخلطها بشكل عشوائي لتكوين كتاب عينات من المحارف. لقد صمد هذا النص لأكثر من خمسة قرون."
            )
        }
    }
}