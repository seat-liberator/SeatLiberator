package com.seatliberator.seatliberator.identity.server.application.user.port.out;

import com.seatliberator.seatliberator.identity.server.domain.account.User;

public interface UserStore {
    User save(User user);

    void delete(User user);
}
