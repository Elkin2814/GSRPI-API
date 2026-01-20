package co.unicauca.gsrpi_api.auth.domain.port.input;

import co.unicauca.gsrpi_api.auth.domain.model.User;
import co.unicauca.gsrpi_api.auth.domain.model.dto.response.UserResponse;

public interface AuthUseCase {

    /**
     * Autentica un usuario por email y contraseña.
     * 
     * @param email    email del usuario
     * @param password contraseña en texto plano
     * @return User autenticado
     * @throws RuntimeException si las credenciales son inválidas
     */
    User login(String email, String password);

    String loginAndGetToken(String email, String password);

    User getUserByUid(String uid);

    UserResponse mapToResponse(User user);

    String getUidFromToken(String token);

    String generateToken(String uid);
}
