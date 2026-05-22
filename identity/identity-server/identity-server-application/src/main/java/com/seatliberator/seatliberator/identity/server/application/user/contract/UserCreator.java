package com.seatliberator.seatliberator.identity.server.application.user.contract;

import com.seatliberator.seatliberator.identity.server.application.role.port.in.RoleGrantor;
import com.seatliberator.seatliberator.identity.server.application.role.service.BootstrapDefaultGrantRegistry;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserStore;
import com.seatliberator.seatliberator.identity.server.domain.account.User;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
@RequiredArgsConstructor
public class UserCreator {
    private final UserStore store;

    private final RoleGrantor roleGrantor;
    private final BootstrapDefaultGrantRegistry bootstrapDefaultGrantRegistry;
    private final Clock clock;

    public User create(String nickname) {
        Preconditions.requireNonNull(nickname, "nickname");

        var now = clock.instant();

        var user = User.of(nickname, now);
        var saved = store.save(user);
        var userId = user.getId();

        var defaultRoles = bootstrapDefaultGrantRegistry.getDefaultNamespaceRole();
        roleGrantor.grantAll(userId.toString(), defaultRoles);

        return saved;
    }
}
