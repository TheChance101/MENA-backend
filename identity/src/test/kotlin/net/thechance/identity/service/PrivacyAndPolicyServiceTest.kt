package net.thechance.identity.service

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import net.thechance.identity.service.model.PrivacyAndPolicySectionModel
import org.junit.Test
import org.springframework.context.MessageSource
import java.util.Locale

class PrivacyAndPolicyServiceTest {
    private val messageSource: MessageSource = mockk(relaxed = true)
    private val privacyAndPolicyService = PrivacyAndPolicyService(messageSource)

    @Test
    fun `getPrivacyAndPolicy() should return english data when en is passed`() {
        every { messageSource.getMessage(any(), any(), any()) } returnsMany localizedResultEn

        val result = privacyAndPolicyService.getPrivacyAndPolicy(localeEn).sections

        assertThat(result).isEqualTo(sectionsModelEn)
    }

    @Test
    fun `getPrivacyAndPolicy() should return arabic data when ar is passed`() {
        every { messageSource.getMessage(any(), any(), any()) } returnsMany localizedResultAr

        val result = privacyAndPolicyService.getPrivacyAndPolicy(localeAr).sections

        assertThat(result).isEqualTo(sectionsModelAr)
    }

    companion object {
        private const val MAX_SECTIONS = 3
        private val localeEn = Locale("en")
        private val localeAr = Locale("ar")

        private const val TITLE_EN = "What is Lorem Ipsum?"
        private const val CONTENT_EN = "is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries."
        private const val TITLE_AR = "ما هو لوريم إيبسوم؟"
        private const val CONTENT_AR = "هو مجرد نص وهمي يُستخدم في صناعات الطباعة وتنسيق النصوص. لقد كان لوريم إيبسوم النص الوهمي القياسي في هذه الصناعة منذ القرن السادس عشر (منذ عام 1500 تقريباً)، عندما أخذت مطبعة مجهولة مجموعة من المحارف وقامت بخلطها بشكل عشوائي لتكوين كتاب عينات من المحارف. لقد صمد هذا النص لأكثر من خمسة قرون."

        private val localizedResultEn = (0 .. MAX_SECTIONS).flatMap { listOf(TITLE_EN, CONTENT_EN) }
        private val localizedResultAr = (0 .. MAX_SECTIONS).flatMap { listOf(TITLE_AR, CONTENT_AR) }

        private val sectionsModelEn = List(3) {
            PrivacyAndPolicySectionModel(
                title = TITLE_EN,
                content = CONTENT_EN
            )
        }

        private val sectionsModelAr = List(3) {
            PrivacyAndPolicySectionModel(
                title = TITLE_AR,
                content = CONTENT_AR
            )
        }
    }
}