package com.seatliberator.seatliberator.identity.server.application.shared.seed;

import com.seatliberator.seatliberator.identity.core.role.InitialNamespaceRoleRegistry;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.server.application.credential.port.in.RegisterCredentialAccountUseCase;
import com.seatliberator.seatliberator.identity.server.application.credential.port.in.command.RegisterCredentialAccountCommand;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.UpdateRoleUseCase;
import com.seatliberator.seatliberator.identity.server.application.role.port.in.command.UpdateRoleCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSeeder {
    private static final String testNicknameFormat = "test_%s";
    private static final String testEmailFormat = "test_%s@example.com";
    private static final String testPasswordFormat = "1234!test_%s";

    private final RegisterCredentialAccountUseCase registerCredentialAccountUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final InitialNamespaceRoleRegistry initialNamespaceRoleRegistry;

    public void seed() {
        var initialNamespaceRoles = initialNamespaceRoleRegistry.getAll();

        for (var role : Role.values()) {
            var roleName = role.name().toLowerCase(Locale.ROOT);
            var nickname = String.format(testNicknameFormat, roleName);
            var email = String.format(testEmailFormat, roleName);
            var password = String.format(testPasswordFormat, roleName);

            var command = RegisterCredentialAccountCommand.of(nickname, email, password);
            var result = registerCredentialAccountUseCase.register(command);
            log.info("Create test user userId={}, nickname={}, email={}, password={}", result.userId(), result.nickname(), email, password);
            var userId = result.userId();

            for (var namespaceRole : initialNamespaceRoles) {
                var updateRoleCommand = UpdateRoleCommand.of(userId, namespaceRole.namespace(), role);
                updateRoleUseCase.update(updateRoleCommand);
            }
        }
    }
}
