package dev.ilkerk.leasing.presentation.customer;

import dev.ilkerk.leasing.application.customer.dto.request.customerPreference.CustomerPreferenceCreateDTO;
import dev.ilkerk.leasing.application.customer.dto.request.customerPreference.CustomerPreferenceFindDTO;
import dev.ilkerk.leasing.application.customer.dto.response.CustomerPreferenceResponse;
import dev.ilkerk.leasing.application.customer.service.CustomerPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("customer-preference")
@Slf4j
@RequiredArgsConstructor
public class CustomerPreferenceController {
	private final CustomerPreferenceService customerPreferenceService;

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<CustomerPreferenceResponse> getById(@PathVariable UUID id) {
		try {
			return customerPreferenceService.get(id);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<CustomerPreferenceResponse> getAll(@ModelAttribute @Valid CustomerPreferenceFindDTO customerPreferenceFindDTO) {
		try {
			return customerPreferenceService.getAllByCriteria(customerPreferenceFindDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Flux.error(e);
		}
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<CustomerPreferenceResponse> create(@RequestBody @Valid CustomerPreferenceCreateDTO customerPreferenceCreateDTO) {
		try {
			return customerPreferenceService.create(customerPreferenceCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<CustomerPreferenceResponse> update(@PathVariable UUID id, @RequestBody @Valid CustomerPreferenceCreateDTO customerPreferenceCreateDTO) {
		try {
			return customerPreferenceService.update(id, customerPreferenceCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable UUID id) {
		return customerPreferenceService.deleteById(id);
	}
}
