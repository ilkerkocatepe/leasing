package dev.ilkerk.leasing.presentation.contract;

import dev.ilkerk.leasing.application.contract.dto.request.transaction.TransactionCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.transaction.TransactionFindDTO;
import dev.ilkerk.leasing.application.contract.dto.response.TransactionResponse;
import dev.ilkerk.leasing.application.contract.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("transaction")
@Slf4j
@RequiredArgsConstructor
public class TransactionController {
	private final TransactionService transactionService;

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<TransactionResponse> getById(@PathVariable UUID id) {
		try {
			return transactionService.get(id);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<TransactionResponse> getAll(@ModelAttribute @Valid TransactionFindDTO transactionFindDTO) {
		try {
			return transactionService.getAllByCriteria(transactionFindDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Flux.error(e);
		}
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<TransactionResponse> create(@RequestBody @Valid TransactionCreateDTO transactionCreateDTO) {
		try {
			return transactionService.create(transactionCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PostMapping("multiple")
	@ResponseStatus(HttpStatus.CREATED)
	public Flux<TransactionResponse> createAll(@RequestBody @Valid List<TransactionCreateDTO> transactionCreateDTO) {
		try {
			return transactionService.createAll(transactionCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Flux.error(e);
		}
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<TransactionResponse> update(@PathVariable UUID id, @RequestBody @Valid TransactionCreateDTO transactionCreateDTO) {
		try {
			return transactionService.update(id, transactionCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable UUID id) {
		return transactionService.deleteById(id);
	}

	@GetMapping("calculate")
	public Mono<Map<UUID, Double>> calculate(@RequestParam UUID contractId) {
		return transactionService.calculateTransactions(contractId, LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.now());
	}
}
