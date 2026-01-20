package co.unicauca.gsrpi_api.auth.infrastructure.input.controller;

import co.unicauca.gsrpi_api.auth.domain.model.User;
import co.unicauca.gsrpi_api.auth.domain.model.dto.request.LoginRequest;
import co.unicauca.gsrpi_api.auth.domain.model.dto.response.LoginResponse;
import co.unicauca.gsrpi_api.auth.domain.model.dto.response.UserResponse;
import co.unicauca.gsrpi_api.auth.domain.port.input.AuthUseCase;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthUseCase authService;

  public AuthController(AuthUseCase authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request,
      HttpServletResponse response) {
    try {
      String token = authService.loginAndGetToken(request.getEmail(), request.getPassword());

      // Cookie HTTP-only
      Cookie cookie = new Cookie("access_token", token);
      cookie.setHttpOnly(true);
      cookie.setPath("/");
      cookie.setMaxAge(3600); // 1 hora
      response.addCookie(cookie);

      return ResponseEntity.ok(new LoginResponse("Login exitoso", token));

    } catch (RuntimeException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new LoginResponse(e.getMessage(), null));
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletResponse response) {

    // Eliminar cookie
    Cookie cookie = new Cookie("access_token", null);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(0); // caduca inmediatamente
    response.addCookie(cookie);

    return ResponseEntity.ok("Logout exitoso");
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> me(@CookieValue(name = "access_token", required = false) String token) {
    if (token == null || token.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    try {
      String uid = authService.getUidFromToken(token);
      User user = authService.getUserByUid(uid);
      UserResponse userResponse = authService.mapToResponse(user);
      return ResponseEntity.ok(userResponse);

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<LoginResponse> refreshToken(
      @CookieValue(name = "access_token", required = false) String token,
      HttpServletResponse response) {

    if (token == null || token.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new LoginResponse("Token no encontrado", null));
    }

    try {
      String uid = authService.getUidFromToken(token);
      // Genera un nuevo token
      String newToken = authService.generateToken(uid);

      // Reemplaza cookie
      Cookie cookie = new Cookie("access_token", newToken);
      cookie.setHttpOnly(true);
      cookie.setPath("/");
      cookie.setMaxAge(3600); // 1 hora
      response.addCookie(cookie);

      return ResponseEntity.ok(new LoginResponse("Token refrescado", newToken));

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new LoginResponse("Token inválido", null));
    }
  }

}
