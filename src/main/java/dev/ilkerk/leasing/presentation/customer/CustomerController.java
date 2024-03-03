package dev.ilkerk.leasing.presentation.customer;

import dev.ilkerk.leasing.application.customer.dto.request.customer.CustomerCreateDTO;
import dev.ilkerk.leasing.application.customer.dto.request.customer.CustomerFindDTO;
import dev.ilkerk.leasing.application.customer.dto.request.customer.CustomerWithAddressCreateDTO;
import dev.ilkerk.leasing.application.customer.dto.response.CustomerResponse;
import dev.ilkerk.leasing.application.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("customer")
@Slf4j
@RequiredArgsConstructor
public class CustomerController {
	private final CustomerService customerService;

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<CustomerResponse> getById(@PathVariable UUID id) {
		try {
			return customerService.get(id);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<CustomerResponse> getAll(@ModelAttribute @Valid CustomerFindDTO customerFindDTO) {
		try {
			return customerService.getAllByCriteria(customerFindDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Flux.error(e);
		}
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<CustomerResponse> create(@RequestBody @Valid CustomerCreateDTO customerCreateDTO) {
		try {
			return customerService.create(customerCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PostMapping("create-with-address")
	@ResponseStatus(HttpStatus.CREATED)
	@PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('DEALER')")
	public Mono<CustomerResponse> createWithAddress(@RequestBody @Valid CustomerWithAddressCreateDTO customerWithAddressCreateDTO) {
		try {
			return customerService.createWithAddress(customerWithAddressCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<CustomerResponse> update(@PathVariable UUID id, @RequestBody @Valid CustomerCreateDTO customerCreateDTO) {
		try {
			return customerService.update(id, customerCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable UUID id) {
		return customerService.deleteById(id);
	}
}
