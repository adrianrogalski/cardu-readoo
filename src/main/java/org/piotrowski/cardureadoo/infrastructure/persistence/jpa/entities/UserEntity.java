package org.piotrowski.cardureadoo.infrastructure.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.piotrowski.cardureadoo.domain.security.UserRole;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "app_user",
        uniqueConstraints = @UniqueConstraint(name = "UK_USER_USERNAME", columnNames = "username"),
        indexes = @Index(name = "IDX_USER_USERNAME", columnList = "username", unique = true)
)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name="password_hash", nullable = false, length = 255)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="user_role", joinColumns = @JoinColumn(name="user_id", foreignKey=@ForeignKey(name="FK_USER_ROLE")))
    @Enumerated(EnumType.STRING)
    @Column(name="role", length = 20, nullable = false)
    private Set<UserRole> roles;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public UserEntity(String username, String passwordHash, Set<UserRole> roles) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = roles;
    }
}
