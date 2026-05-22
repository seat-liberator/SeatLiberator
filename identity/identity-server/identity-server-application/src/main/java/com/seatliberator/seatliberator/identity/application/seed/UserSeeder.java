package com.seatliberator.seatliberator.identity.application.seed;

import com.seatliberator.seatliberator.board.api.BoardApi;
import com.seatliberator.seatliberator.identity.application.port.in.UserRegistrar;
import com.seatliberator.seatliberator.identity.application.port.in.command.RegistrationCommand;
import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.SimpleNamespaceRole;
import com.seatliberator.seatliberator.kernel.ApplicationNamespace;
import com.seatliberator.seatliberator.reservation.api.ReservationApi;
import com.seatliberator.seatliberator.role.application.port.in.RoleGrantor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSeeder {
    private static final String testNicknameFormat = "test_%s";
    private static final String testEmailFormat = "test_%s@example.com";
    private static final String testPasswordFormat = "1234!test_%s";
    private static final List<ApplicationNamespace> grantTargetServices = List.of(ReservationApi.NAMESPACE, BoardApi.NAMESPACE);

    private final UserRegistrar userRegistrar;
    private final RoleGrantor roleGrantor;

    public void seed() {
        for (var role : Role.values()) {
            var roleName = role.name().toLowerCase(Locale.ROOT);
            var nickname = String.format(testNicknameFormat, roleName);
            var email = String.format(testEmailFormat, roleName);
            var password = String.format(testPasswordFormat, roleName);
            log.info("Create test user nickname={}, email={}, password={}", nickname, email, password);

            var command = new RegistrationCommand.Credential(nickname, email, password);
            var auth = userRegistrar.register(command);
            var userId = auth.userId().toString();
            var namespaceRoles = grantTargetServices.stream()
                    .<NamespaceRole>map(ns -> SimpleNamespaceRole.from(ns, role))
                    .toList();
            roleGrantor.grantAll(userId, namespaceRoles);
        }
    }
}
