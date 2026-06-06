package ru.skypro.homework.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import ru.skypro.homework.constant.ApiConstants;
import ru.skypro.homework.dto.Login;
import ru.skypro.homework.dto.Register;

/**
 * REST-контроллер регистрации и авторизации пользователей.
 * <p>
 * На Этапе I возвращает только HTTP-статусы без бизнес-логики.
 */
@CrossOrigin(origins = ApiConstants.CORS_ORIGIN)
@RestController
public class AuthController {

    /**
     * Регистрирует нового пользователя.
     *
     * @param register данные регистрации из тела запроса
     * @return {@code 201 Created} при успешной регистрации
     */
    @Operation(summary = "Регистрация пользователя", operationId = "register", tags = {"Регистрация"})
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody Register register) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Авторизует пользователя по логину и паролю.
     *
     * @param login учётные данные из тела запроса
     * @return {@code 200 OK} при успешной авторизации
     */
    @Operation(summary = "Авторизация пользователя", operationId = "login", tags = {"Авторизация"})
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody Login login) {
        return ResponseEntity.ok().build();
    }
}
