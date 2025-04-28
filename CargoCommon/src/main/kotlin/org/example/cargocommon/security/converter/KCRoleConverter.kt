package org.example.cargocommon.security.converter

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt

class KCRoleConverter : Converter<Jwt, Collection<GrantedAuthority>> {

    companion object {
        private const val REALM_ACCESS = "realm_access"
        private const val ROLES = "roles"
    }

    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val resourceAccess = jwt.claims[REALM_ACCESS] as? Map<*, *> ?: return emptyList()
        val roles = resourceAccess[ROLES] as? List<*> ?: return emptyList()

        return roles
            .filterIsInstance<String>()
            .map { role -> SimpleGrantedAuthority("ROLE_$role") }
    }
}