package com.seatliberator.seatliberator.role.application.port.in;

import java.util.List;

public interface RoleReader {
    List<UserGrantedRoleEntry> read(String userId);
}
