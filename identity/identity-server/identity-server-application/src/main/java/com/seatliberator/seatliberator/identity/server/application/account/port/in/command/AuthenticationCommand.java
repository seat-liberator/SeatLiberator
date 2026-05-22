package com.seatliberator.seatliberator.identity.server.application.account.port.in.command;

public class AuthenticationCommand {
    public record Credential(
            String email,
            String password
    ) {
    }

    public record Federated(
            String registrationId,
            String providerUserId
    ) {
    }
}
