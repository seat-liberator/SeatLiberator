package com.seatliberator.seatliberator.identity.server.user;

import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.user.port.in.UpdateNicknameUseCase;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserStore;
import com.seatliberator.seatliberator.identity.server.application.user.service.UpdateNicknameService;
import com.seatliberator.seatliberator.identity.server.domain.account.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.seatliberator.seatliberator.identity.server.user.UserUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateNicknameUseCase 테스트")
public class UpdateNicknameUseCaseTest {
    @Mock
    UserReader reader;

    @Mock
    UserStore store;

    UpdateNicknameUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new UpdateNicknameService(reader, store, CLOCK);
    }

    @Test
    @DisplayName("사용자를 조회하고 변경된 사용자를 저장한다")
    void find_user_and_save_updated_user() {
        when(reader.findById(USER_ID))
                .thenReturn(Optional.of(user()));
        when(store.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        useCase.update(updateNicknameCommand());

        verify(reader).findById(USER_ID);
        verify(store).save(any(User.class));
    }

    @Test
    @DisplayName("닉네임을 변경할 사용자가 없으면 예외")
    void throw_exception_when_user_not_found() {
        when(reader.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatApplicationThrownBy(() -> useCase.update(updateNicknameCommand()))
                .hasErrorCode(IdentityApplicationErrorCode.USER_NOT_FOUND);
    }
}
