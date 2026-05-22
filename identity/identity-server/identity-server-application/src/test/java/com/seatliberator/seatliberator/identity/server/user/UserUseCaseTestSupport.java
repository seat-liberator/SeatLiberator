package com.seatliberator.seatliberator.identity.server.user;

import com.seatliberator.seatliberator.identity.server.application.user.port.in.command.UpdateNicknameCommand;
import com.seatliberator.seatliberator.identity.server.domain.account.User;
import com.seatliberator.seatliberator.identity.server.domain.account.UserFixture;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.kernel.test.clock.TestClock;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class UserUseCaseTestSupport {
    public static final Clock CLOCK = TestClock.getFixed();

    public static final UUID USER_ID = UuidGenerator.generate(1);
    public static final String NICKNAME = "nickname";
    public static final Instant USER_CREATED_AT = CLOCK.instant();
    public static final String UPDATED_NICKNAME = "updated-nickname";

    public static User user() {
        var user = new UserFixture.Builder()
                .nickname(NICKNAME)
                .createdAt(USER_CREATED_AT)
                .build();
        stubId(user, USER_ID);
        return user;
    }

    public static UpdateNicknameCommand updateNicknameCommand() {
        return UpdateNicknameCommand.of(USER_ID, UPDATED_NICKNAME);
    }

    public static void stubId(User user, UUID id) {
        try {
            var idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("테스트용 ID 설정 실패");
        }
    }
}
