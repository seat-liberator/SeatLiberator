package com.seatliberator.seatliberator.identity.server.domain.account;

import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAccount {
    public abstract void assignUser(User user);

    public abstract User getUser();
}
