package net.thechance.identity.api.controller.settings

import net.thechance.identity.api.dto.settings.ContactInfoResponse
import net.thechance.identity.api.dto.settings.PrivacyAndPolicyResponse
import net.thechance.identity.api.mapper.toResponse
import net.thechance.identity.service.ContactInfoService
import net.thechance.identity.service.PrivacyAndPolicyService
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/identity/settings")
class SettingsController(
    private val privacyAndPolicyService: PrivacyAndPolicyService,
    private val contactInfoService: ContactInfoService,
) {
    @GetMapping("/privacy-and-policy")
    fun getPrivacyAndPolicy(): ResponseEntity<PrivacyAndPolicyResponse> {
        val language = LocaleContextHolder.getLocale().language
        val privacyAndPolicy = privacyAndPolicyService.getPrivacyAndPolicy(language)
        return ResponseEntity.ok(privacyAndPolicy.toResponse())
    }

    @GetMapping("/contact-info")
    fun getContactInfo(): ResponseEntity<ContactInfoResponse> {
        val contactInfo = contactInfoService.getContactInfo()
        return ResponseEntity.ok(contactInfo.toResponse())
    }
}
