package dev.ilkerk.leasing.presentation.contract;

import dev.ilkerk.leasing.application.contract.dto.request.transport.TransportCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.transport.TransportFindDTO;
import dev.ilkerk.leasing.domain.contract.entity.Transport;
import dev.ilkerk.leasing.application.contract.service.TransportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("transport")
@Slf4j
@RequiredArgsConstructor
public class TransportController {
	private final TransportService transportService;

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<Transport> getById(@PathVariable UUID id) {
		try {
			return transportService.get(id);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<Transport> getAll(@ModelAttribute @Valid TransportFindDTO transportFindDTO) {
		try {
			return transportService.getAllByCriteria(transportFindDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Flux.error(e);
		}
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Transport> create(@RequestBody @Valid TransportCreateDTO transportCreateDTO) {
		try {
			return transportService.create(transportCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<Transport> update(@PathVariable UUID id, @RequestBody @Valid TransportCreateDTO transportCreateDTO) {
		try {
			return transportService.update(id, transportCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable UUID id) {
		return transportService.deleteById(id);
	}
}
