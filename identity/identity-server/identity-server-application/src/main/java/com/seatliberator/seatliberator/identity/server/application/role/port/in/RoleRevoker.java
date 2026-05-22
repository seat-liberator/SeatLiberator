package com.seatliberator.seatliberator.identity.server.application.role.port.in;

public interface RoleRevoker {
    void revoke(String userId, String namespace);
}
