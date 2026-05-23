package com.seatliberator.seatliberator.identity.server.role;

import com.seatliberator.seatliberator.identity.server.application.role.port.in.FindUserGrantedSummaryUseCase;
import com.seatliberator.seatliberator.identity.server.application.role.port.out.UserGrantedRoleReader;
import com.seatliberator.seatliberator.identity.server.application.role.service.UserGrantedRoleQueryService;
import com.seatliberator.seatliberator.identity.server.application.shared.exception.IdentityApplicationErrorCode;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.seatliberator.seatliberator.identity.server.role.RoleUseCaseTestSupport.*;
import static com.seatliberator.seatliberator.kernel.test.assertion.ApplicationAssertions.assertThatApplicationThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindUserGrantedSummaryUseCase 테스트")
public class FindUserGrantedSummaryUseCaseTest {
    @Mock
    UserGrantedRoleReader reader;

    @Mock
    UserReader userReader;

    FindUserGrantedSummaryUseCase useCase;

    @BeforeEach
    void run() {
        useCase = new UserGrantedRoleQueryService(reader, userReader);
    }

    @Test
    @DisplayName("사용자가 있으면 부여된 권한 summary를 조회한다")
    void find_user_granted_summary_when_user_exists() {
        var query = findUserGrantedSummaryQuery();
        var expected = userGrantedSummaryResult();
        var grantedRole = userGrantedRole();
        when(userReader.existsById(USER_ID)).thenReturn(true);
        when(reader.findByUserId(USER_ID)).thenReturn(List.of(grantedRole));

        var result = useCase.find(query);

        verify(userReader).existsById(USER_ID);
        verify(reader).findByUserId(USER_ID);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("사용자가 없으면 예외")
    void throw_exception_when_user_not_found() {
        when(userReader.existsById(USER_ID)).thenReturn(false);

        assertThatApplicationThrownBy(() -> useCase.find(findUserGrantedSummaryQuery()))
                .hasErrorCode(IdentityApplicationErrorCode.USER_NOT_FOUND);

        verify(userReader).existsById(USER_ID);
        verifyNoInteractions(reader);
    }
}
