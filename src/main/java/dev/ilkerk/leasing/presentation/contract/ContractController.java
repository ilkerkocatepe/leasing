package dev.ilkerk.leasing.presentation.contract;

import dev.ilkerk.leasing.application.contract.dto.request.contract.ContractCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.contract.ContractFindDTO;
import dev.ilkerk.leasing.application.contract.dto.request.contract.TransactionWithContractCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.contract.TransactionWithContractUpdateDTO;
import dev.ilkerk.leasing.application.contract.dto.response.ContractResponse;
import dev.ilkerk.leasing.application.contract.service.ContractService;
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
@RequestMapping("contract")
@Slf4j
@RequiredArgsConstructor
public class ContractController {
	private final ContractService contractService;

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ContractResponse> getById(@PathVariable UUID id) {
		try {
			return contractService.get(id);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<ContractResponse> getAll(@ModelAttribute @Valid ContractFindDTO contractFindDTO) {
		try {
			return contractService.getAllByCriteria(contractFindDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Flux.error(e);
		}
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ContractResponse> create(@RequestBody @Valid ContractCreateDTO contractCreateDTO) {
		try {
			return contractService.create(contractCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PostMapping("create/with-transaction")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<ContractResponse> createWithTransaction(@RequestBody @Valid TransactionWithContractCreateDTO transactionWithContractCreateDTO, Authentication authentication) {
		try {
			return contractService.createWithTransaction(transactionWithContractCreateDTO, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ContractResponse> update(@PathVariable UUID id, @RequestBody @Valid ContractCreateDTO contractCreateDTO) {
		try {
			return contractService.update(id, contractCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PutMapping("update/with-transaction/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ContractResponse> updateWithTransaction(@PathVariable UUID id, @RequestBody @Valid TransactionWithContractUpdateDTO transactionWithContractUpdateDTO) {
		try {
			return contractService.updateWithTransaction(id, transactionWithContractUpdateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable UUID id) {
		return contractService.deleteById(id);
	}

	@PostMapping("{id}/add-note")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ContractResponse> addNoteToContract(@PathVariable UUID id, @RequestParam String note, Authentication authentication) {
		try {
			return contractService.addNoteToContract(id, note, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PostMapping("preview")
	@ResponseStatus(HttpStatus.OK)
	public Mono<ContractResponse> previewContract(@RequestBody @Valid TransactionWithContractCreateDTO transactionWithContractCreateDTO, Authentication authentication) {
		try {
			return contractService.preview(transactionWithContractCreateDTO, authentication);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}
}
