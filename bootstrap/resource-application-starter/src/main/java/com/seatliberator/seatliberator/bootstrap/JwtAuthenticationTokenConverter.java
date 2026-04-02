package com.seatliberator.seatliberator.bootstrap;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

@FunctionalInterface
public interface JwtAuthenticationTokenConverter extends Converter<Jwt, AbstractAuthenticationToken> {
}
