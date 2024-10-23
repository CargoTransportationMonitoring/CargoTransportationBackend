package org.example.cargotransporationmonitoring

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class LocaleTest: AbstractTest() {

    @ParameterizedTest
    @CsvSource(
        "en, Locale changed to en.",
        "ru, Локаль изменена на ru."
    )
    fun `setLocale - valid locale - locale successfully changed`(locale: String, expectedResponse: String) {
        val response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/setLocale")
            .param("lang", locale))
            .andExpect(status().isOk)
            .andReturn().response.contentAsString

        Assertions.assertEquals(expectedResponse, response)
    }

    @Test
    fun `setLocale - invalid locale - error locale changed`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/setLocale")
            .param("lang", "fr"))
            .andExpect(status().isBadRequest)
            .andReturn().response.contentAsString
    }

}