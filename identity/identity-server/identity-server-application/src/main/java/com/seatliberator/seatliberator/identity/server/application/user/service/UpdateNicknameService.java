package com.seatliberator.seatliberator.identity.server.application.user.service;

import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationException;
import com.seatliberator.seatliberator.identity.server.application.user.port.in.UpdateNicknameUseCase;
import com.seatliberator.seatliberator.identity.server.application.user.port.in.command.UpdateNicknameCommand;
import com.seatliberator.seatliberator.identity.server.application.user.port.in.result.UserResult;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateNicknameService implements UpdateNicknameUseCase {
    private final UserReader reader;
    private final UserStore store;

    private final Clock clock;

    @Override
    public UserResult update(UpdateNicknameCommand command) {
        var userId = command.userId();
        var user = reader.findById(userId)
                .orElseThrow(() -> new IdentityApplicationException(IdentityApplicationErrorCode.USER_NOT_FOUND));

        var nickname = command.nickname();
        var now = clock.instant();
        user.updateNickname(nickname, now);

        var saved = store.save(user);

        return UserResult.from(saved);
    }
}
