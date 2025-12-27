package co.unicauca.gsrpi_api.auth.infrastructure.output.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "rol")
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private long roleId;
    @Column(name = "nombre", columnDefinition = "TEXT", unique = true, nullable = false)
    private String name;
    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = true)
    private String description;

    //Relacion one to many con usuarioRol
    @OneToMany(cascade = {CascadeType.ALL},fetch = FetchType.LAZY, mappedBy = "role")
    private List<UserRoleEntity> userRoles;

    public RoleEntity() {
    }

    public RoleEntity(long roleId, String name, String description, List<UserRoleEntity> userRoles) {
        this.roleId = roleId;
        this.name = name;
        this.description = description;
        this.userRoles = userRoles;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserRoleEntity> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRoleEntity> userRoles) {
        this.userRoles = userRoles;
    }
}
