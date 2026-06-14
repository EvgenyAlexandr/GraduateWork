package ru.skypro.homework.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import ru.skypro.homework.constant.ApiConstants;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;
import ru.skypro.homework.service.AuthService;

/**
 * REST-контроллер регистрации и авторизации пользователей.
 */
@CrossOrigin(origins = ApiConstants.CORS_ORIGIN)
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Регистрирует нового пользователя в БД.
     *
     * @param register данные регистрации из тела запроса
     * @return {@code 201 Created} при успехе; {@code 400} если email уже занят или данные невалидны
     */
    @Operation(summary = "Регистрация пользователя", operationId = "register", tags = {"Регистрация"})
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody Register register) {
        if (!authService.register(register)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Проверяет логин и пароль пользователя.
     *
     * @param login учётные данные из тела запроса
     * @return {@code 200 OK} при успехе; {@code 401} при неверных данных
     */
    @Operation(summary = "Авторизация пользователя", operationId = "login", tags = {"Авторизация"})
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid @RequestBody Login login) {
        if (!authService.login(login.getUsername(), login.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }
}
