package com.bankapplication.model;

import com.bankapplication.model.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "users")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String name;

    private String userType;
    private boolean canRegisterAccounts;
    private boolean needsNextOfKin;

    @ManyToMany(mappedBy = "roles")
    @JsonBackReference
    private Set<User> users;

    public Role(UserRole roleEnum) {
        this.name = roleEnum.name();
        if (roleEnum == UserRole.CUSTOMER) {
            this.needsNextOfKin = true;
        }
    }


}
