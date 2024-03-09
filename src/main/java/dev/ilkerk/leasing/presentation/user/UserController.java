package dev.ilkerk.leasing.presentation.user;

import dev.ilkerk.leasing.application.user.dto.request.user.ChangePasswordRequest;
import dev.ilkerk.leasing.application.user.dto.request.user.UserCreateDTO;
import dev.ilkerk.leasing.application.user.dto.request.user.UserFindDTO;
import dev.ilkerk.leasing.application.user.dto.response.UserResponse;
import dev.ilkerk.leasing.application.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("user")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserResponse> getById(@PathVariable UUID id) {
        try {
            return userService.get(id);
        } catch (Exception e) {
            log.error(e.getMessage());

            return Mono.error(e);
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<UserResponse> getAll(@ModelAttribute @Valid UserFindDTO userFindDTO) {
        try {
            return userService.getAllByCriteria(userFindDTO);
        } catch (Exception e) {
            log.error(e.getMessage());

            return Flux.error(e);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponse> create(@RequestBody @Valid UserCreateDTO userCreateDTO) {
        try {
            return userService.create(userCreateDTO);
        } catch (Exception e) {
            log.error(e.getMessage());

            return Mono.error(e);
        }
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserResponse> update(@PathVariable UUID id, @RequestBody @Valid UserCreateDTO userCreateDTO) {
        try {
            return userService.update(id, userCreateDTO);
        } catch (Exception e) {
            log.error(e.getMessage());

            return Mono.error(e);
        }
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id) {
        return userService.deleteById(id);
    }

    @PutMapping("change-password")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest, Authentication authentication) {
        return userService.changePassword(changePasswordRequest, authentication);
    }
}
