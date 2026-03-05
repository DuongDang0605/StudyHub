package org.example.studyhub.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Setting role;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class UserRoleId implements Serializable {

        @Column(name = "user_id")
        private Long userId;

        @Column(name = "role_id")
        private Long roleId;
    }
}