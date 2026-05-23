package com.seatliberator.seatliberator.identity.server.domain.role;

import com.seatliberator.seatliberator.identity.core.role.NamespaceRole;
import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "user_granted_role",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_granted_role_user_id_namespace",
                        columnNames = {"user_id", "namespace"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGrantedRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "namespace", column = @Column(name = "namespace", nullable = false)),
            @AttributeOverride(name = "role", column = @Column(name = "role", nullable = false))
    })
    private EmbeddableNamespaceRole namespaceRole;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    private UserGrantedRole(UUID userId, EmbeddableNamespaceRole namespaceRole, Instant createdAt) {
        this.userId = Preconditions.requireNonNull(userId, "userId");
        this.namespaceRole = Preconditions.requireNonNull(namespaceRole, "namespaceRole");
        this.createdAt = Preconditions.requireNonNull(createdAt, "createdAt");
    }

    public static UserGrantedRole of(UUID userId, NamespaceRole namespaceRole, Instant createdAt) {
        Preconditions.requireNonNull(namespaceRole, "namespaceRole");

        return new UserGrantedRole(userId, EmbeddableNamespaceRole.from(namespaceRole), createdAt);
    }

    public void updateRole(Role role) {
        Preconditions.requireNonNull(role, "role");

        this.namespaceRole = namespaceRole.withRole(role);
    }
}
