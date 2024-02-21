package dev.ilkerk.leasing.presentation.contract;

import dev.ilkerk.leasing.application.contract.dto.request.contract.*;
import dev.ilkerk.leasing.application.contract.dto.response.DiscountResponse;
import dev.ilkerk.leasing.application.contract.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("discount")
@Slf4j
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<DiscountResponse> getById(@PathVariable UUID id) {
        try {
            return discountService.get(id);
        } catch (Exception e) {
            log.error(e.getMessage());

            return Mono.error(e);
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<DiscountResponse> getAll(@ModelAttribute @Valid DiscountFind discountFind) {
        try {
            return discountService.getAllByCriteria(discountFind);
        } catch (Exception e) {
            log.error(e.getMessage());

            return Flux.error(e);
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DiscountResponse> create(@RequestBody @Valid DiscountCreate discountCreate) {
        try {
            return discountService.create(discountCreate);
        } catch (Exception e) {
            log.error(e.getMessage());

            return Mono.error(e);
        }
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<DiscountResponse> update(@PathVariable UUID id, @RequestBody @Valid DiscountCreate discountCreate) {
        try {
            return discountService.update(id, discountCreate);
        } catch (Exception e) {
            log.error(e.getMessage());

            return Mono.error(e);
        }
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable UUID id) {
        return discountService.deleteById(id);
    }
}
