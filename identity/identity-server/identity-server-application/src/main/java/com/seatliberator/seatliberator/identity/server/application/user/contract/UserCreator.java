package com.seatliberator.seatliberator.identity.server.application.user.contract;

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

    private final Clock clock;

    public User create(String nickname) {
        Preconditions.requireNonNull(nickname, "nickname");

        var now = clock.instant();
        var user = User.of(nickname, now);

        return store.save(user);
    }
}
