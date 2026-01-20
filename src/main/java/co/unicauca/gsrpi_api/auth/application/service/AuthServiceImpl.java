package co.unicauca.gsrpi_api.auth.application.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import co.unicauca.gsrpi_api.auth.domain.model.Role;
import co.unicauca.gsrpi_api.auth.domain.model.User;
import co.unicauca.gsrpi_api.auth.domain.model.UserRole;
import co.unicauca.gsrpi_api.auth.domain.model.dto.response.RoleResponse;
import co.unicauca.gsrpi_api.auth.domain.model.dto.response.UserResponse;
import co.unicauca.gsrpi_api.auth.domain.model.dto.response.UserRoleResponse;
import co.unicauca.gsrpi_api.auth.domain.port.input.AuthUseCase;
import co.unicauca.gsrpi_api.auth.infrastructure.config.JwtUtil;
import co.unicauca.gsrpi_api.auth.infrastructure.output.entity.UserEntity;
import co.unicauca.gsrpi_api.auth.infrastructure.output.entity.UserRoleEntity;
import co.unicauca.gsrpi_api.auth.infrastructure.output.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthUseCase {
    private final String jwtSecret = "TuClaveSecretaSuperSegura";
    private final long jwtExpirationMs = 1000 * 60 * 60 * 24;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User login(String email, String password) {

        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new RuntimeException("Credenciales inválidas");
        }

        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return mapToDomain(userEntity);
    }

    @Override
    public String loginAndGetToken(String email, String password) {
        User user = login(email, password);

        // Generar JWT con uid del usuario
        String token = jwtUtil.generateToken(user.getUid());

        return token;
    }

    @Override
    public User getUserByUid(String uid) {
        UserEntity userEntity = userRepository.findByUid(uid);
        if (userEntity == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        return mapToDomain(userEntity);
    }

    public String getUidFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret.getBytes())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // asumimos que el subject es UID
    }

    public String generateToken(String uid) {
        return Jwts.builder()
                .setSubject(uid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
                .compact();
    }

    /**
     * Mapea UserEntity → User (domain)
     */
    private User mapToDomain(UserEntity entity) {

        User user = new User();
        user.setUserId(entity.getUserId());
        user.setUid(entity.getUid());
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setPassword(null); // nunca devolver password

        if (entity.getUserRoles() != null) {
            List<UserRole> userRoles = entity.getUserRoles()
                    .stream()
                    .map(this::mapUserRoleToDomain)
                    .collect(Collectors.toList());

            user.setUserRoles(userRoles);
        }

        return user;
    }

    private UserRole mapUserRoleToDomain(UserRoleEntity entity) {

        Role role = new Role();
        role.setRoleId(entity.getRole().getRoleId());
        role.setName(entity.getRole().getName());
        role.setDescription(entity.getRole().getDescription());

        UserRole userRole = new UserRole();
        userRole.setUserRoleId(entity.getUserRoleId());
        userRole.setRole(role);

        return userRole;
    }

    public UserResponse mapToResponse(User user) {
        if (user == null)
            return null;

        List<UserRoleResponse> userRolesResponse = null;
        if (user.getUserRoles() != null) {
            userRolesResponse = user.getUserRoles().stream().map(userRole -> {
                UserRoleResponse urr = new UserRoleResponse();
                urr.setUserRoleId(userRole.getUserRoleId());

                if (userRole.getRole() != null) {
                    RoleResponse rr = new RoleResponse();
                    rr.setRoleId(userRole.getRole().getRoleId());
                    rr.setName(userRole.getRole().getName());
                    rr.setDescription(userRole.getRole().getDescription());
                    urr.setRole(rr);
                }
                return urr;
            }).collect(Collectors.toList());
        }

        UserResponse ur = new UserResponse();
        ur.setUserId(user.getUserId());
        ur.setUid(user.getUid());
        ur.setUsername(user.getUsername());
        ur.setEmail(user.getEmail());
        ur.setPassword(null); // nunca devolver password
        ur.setUserRoles(userRolesResponse);

        return ur;
    }
}
