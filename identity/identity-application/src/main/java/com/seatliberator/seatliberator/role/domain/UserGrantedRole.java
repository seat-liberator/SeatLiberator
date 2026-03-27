package com.seatliberator.seatliberator.role.domain;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user_granted_role")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGrantedRole implements NamespaceRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "namespace", nullable = false)
    private String namespace;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    public UserGrantedRole(
            String userId,
            String namespace,
            Role role
    ) {
        this.userId = userId;
        this.namespace = namespace;
        this.role = role;
    }

    public static UserGrantedRole from(
            String userId,
            String namespace,
            Role role
    ) {
        return new UserGrantedRole(userId, namespace, role);
    }

    public static UserGrantedRole copyOf(String userId, NamespaceRole namespaceRole) {
        return new UserGrantedRole(userId, namespaceRole.namespace(), namespaceRole.role());
    }

    @Override
    public String namespace() {
        return namespace;
    }

    @Override
    public Role role() {
        return role;
    }
}
