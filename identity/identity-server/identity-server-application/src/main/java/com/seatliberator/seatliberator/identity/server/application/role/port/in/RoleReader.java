package com.seatliberator.seatliberator.identity.server.application.role.port.in;

import java.util.List;

public interface RoleReader {
    List<UserGrantedRoleEntry> read(String userId);
}
