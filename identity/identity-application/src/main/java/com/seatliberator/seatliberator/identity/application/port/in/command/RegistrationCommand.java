package com.seatliberator.seatliberator.identity.application.port.in.command;

public class RegistrationCommand {
    public record Credential(
            String nickname,
            String email,
            String password
    ) {
    }

    public record Federated(
            String nickname,
            String registrationId,
            String providerUserId
    ) {
    }
}
