package com.seatliberator.seatliberator.role.application.port.in;

public interface RoleRevoker {
    void revoke(String userId, String namespace);
}
