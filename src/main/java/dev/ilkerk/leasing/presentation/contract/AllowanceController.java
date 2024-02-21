package dev.ilkerk.leasing.presentation.contract;

import dev.ilkerk.leasing.application.contract.dto.request.contract.AllowanceCreate;
import dev.ilkerk.leasing.application.contract.dto.request.contract.AllowanceFind;
import dev.ilkerk.leasing.application.contract.dto.response.AllowanceResponse;
import dev.ilkerk.leasing.application.contract.service.AllowanceService;
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
@RequestMapping("allowance")
@Slf4j
@RequiredArgsConstructor
public class AllowanceController {
    private final AllowanceService allowanceService;
    
    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AllowanceResponse> getById(@PathVariable UUID id) {
        try {
            return allowanceService.get(id);
        } catch (Exception e) {
            log.error(e.getMessage());

            return Mono.error(e);
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<AllowanceResponse> getAll(@ModelAttribute @Valid AllowanceFind allowanceFind) {
        try {
            return allowanceService.getAllByCriteria(allowanceFind);
        } catch (Exception e) {
            log.error(e.getMessage());

            return Flux.error(e);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AllowanceResponse> create(@RequestBody @Valid AllowanceCreate allowanceCreate, Authentication authentication) {
        return allowanceService.create(allowanceCreate, authentication.getName());
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AllowanceResponse> update(@PathVariable UUID id, @RequestBody @Valid AllowanceCreate allowanceCreate) {
        return allowanceService.update(id, allowanceCreate);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id) {
        return allowanceService.deleteById(id);
    }

    @PostMapping("preview")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AllowanceResponse> previewContract(@RequestBody AllowanceCreate allowanceCreate) {
        return allowanceService.preview(allowanceCreate);
    }
}
