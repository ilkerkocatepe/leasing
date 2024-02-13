package dev.ilkerk.leasing.presentation.customer;

import dev.ilkerk.leasing.application.customer.dto.request.address.AddressCreateDTO;
import dev.ilkerk.leasing.application.customer.dto.request.address.AddressFindDTO;
import dev.ilkerk.leasing.application.customer.dto.response.AddressResponse;
import dev.ilkerk.leasing.application.customer.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("address")
@Slf4j
@RequiredArgsConstructor
public class AddressController {
	private final AddressService addressService;

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<AddressResponse> getById(@PathVariable UUID id) {
		try {
			return addressService.get(id);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<AddressResponse> getAll(@ModelAttribute @Valid AddressFindDTO addressFindDTO) {
		try {
			return addressService.getAllByCriteria(addressFindDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Flux.error(e);
		}
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<AddressResponse> create(@RequestBody @Valid AddressCreateDTO addressCreateDTO) {
		try {
			return addressService.create(addressCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<AddressResponse> update(@PathVariable UUID id, @RequestBody @Valid AddressCreateDTO addressCreateDTO) {
		try {
			return addressService.update(id, addressCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable UUID id) {
		return addressService.deleteById(id);
	}
}
