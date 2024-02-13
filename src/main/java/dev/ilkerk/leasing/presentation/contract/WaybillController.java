package dev.ilkerk.leasing.presentation.contract;

import dev.ilkerk.leasing.application.contract.dto.request.waybill.WaybillCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.waybill.WaybillFindDTO;
import dev.ilkerk.leasing.domain.contract.entity.Waybill;
import dev.ilkerk.leasing.application.contract.service.WaybillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("waybill")
@Slf4j
@RequiredArgsConstructor
public class WaybillController {
	private final WaybillService waybillService;

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<Waybill> getById(@PathVariable UUID id) {
		try {
			return waybillService.get(id);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<Waybill> getAll(@ModelAttribute @Valid WaybillFindDTO waybillFindDTO) {
		try {
			return waybillService.getAllByCriteria(waybillFindDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Flux.error(e);
		}
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Waybill> create(@RequestBody @Valid WaybillCreateDTO waybillCreateDTO) {
		try {
			return waybillService.create(waybillCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<Waybill> update(@PathVariable UUID id, @RequestBody @Valid WaybillCreateDTO waybillCreateDTO) {
		try {
			return waybillService.update(id, waybillCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable UUID id) {
		return waybillService.deleteById(id);
	}
}
