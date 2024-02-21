package dev.ilkerk.leasing.application.contract.service;

import dev.ilkerk.leasing.application.contract.dto.request.transaction.TransactionCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.transaction.TransactionFindDTO;
import dev.ilkerk.leasing.application.contract.dto.response.TransactionResponse;
import dev.ilkerk.leasing.application.product.service.ProductService;
import dev.ilkerk.leasing.domain.contract.entity.Transaction;
import dev.ilkerk.leasing.domain.contract.entity.TransportType;
import dev.ilkerk.leasing.domain.contract.exception.ProductNotFoundForContractException;
import dev.ilkerk.leasing.domain.contract.exception.WrongAmountTransactionException;
import dev.ilkerk.leasing.domain.contract.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
	private final TransactionRepository transactionRepository;
	private final ModelMapper modelMapper;
	private final ProductService productService;

	public Mono<TransactionResponse> get(UUID id) {
		return this.getObject(id)
				.flatMap(transaction -> Mono.just(modelMapper.map(transaction, TransactionResponse.class)))
				.flatMap(transactionResponse -> productService.get(transactionResponse.getProductId())
						.flatMap(productResponse -> {
							transactionResponse.setProduct(productResponse);

							return Mono.just(transactionResponse);
						}));
	}

	private Mono<Transaction> getObject(UUID id) {
		return transactionRepository.findById(id);
	}

	public Flux<TransactionResponse> getAllByCriteria(TransactionFindDTO transactionFindDTO) {
		Transaction transaction = modelMapper.map(transactionFindDTO, Transaction.class);

		Example<Transaction> transactionExample = Example.of(transaction, TransactionFindDTO.getExampleMatcher());

		return transactionRepository.findAll(transactionExample)
				.flatMap(transaction1 -> Mono.just(modelMapper.map(transaction1, TransactionResponse.class)))
				.flatMap(transactionResponse -> productService.get(transactionResponse.getProductId())
						.flatMap(productResponse -> {
							transactionResponse.setProduct(productResponse);

							return Mono.just(transactionResponse);
						}));
	}

	public Flux<TransactionResponse> getAllByContractId(UUID contractId) {
		return transactionRepository.findAllByContractId(contractId)
				.flatMap(transaction -> Mono.just(modelMapper.map(transaction, TransactionResponse.class)))
				.flatMap(transactionResponse -> productService.get(transactionResponse.getProductId())
						.flatMap(productResponse -> {
							transactionResponse.setProduct(productResponse);

							return Mono.just(transactionResponse);
						}));
	}

	public Mono<TransactionResponse> create(TransactionCreateDTO transactionCreateDTO) {
		assert transactionCreateDTO.getContractId() != null;

		log.info("Transaction creating: " + transactionCreateDTO);

		return Mono.from(transactionRepository.findAllByContractId(transactionCreateDTO.getContractId()))
				.flatMap(transaction -> Mono.just(modelMapper.map(transaction, TransactionResponse.class)))
				.flatMap(transactionResponse -> {
					this.isTransactionValid(List.of(transactionResponse), transactionCreateDTO.getType(), transactionCreateDTO.getProductId(), transactionCreateDTO.getAmount());
                    return Mono.just(transactionResponse);
                })
				.flatMap(transactionResponse -> {
					Transaction transaction = modelMapper.map(transactionCreateDTO, Transaction.class);
					return Mono.just(transaction);
				})
				.flatMap(transactionRepository::save)
				.flatMap(transaction1 -> Mono.just(modelMapper.map(transaction1, TransactionResponse.class)))
				.flatMap(transactionResponse -> productService.get(transactionResponse.getProductId())
						.flatMap(productResponse -> {
							transactionResponse.setProduct(productResponse);

							return Mono.just(transactionResponse);
						}));
	}

	public Flux<TransactionResponse> createAll(List<TransactionCreateDTO> transactionCreateDTOList) {
		log.info("All transactions creating: " + transactionCreateDTOList.toString());

//		List<Transaction> transactionList = new ArrayList<>();

		return Flux.fromIterable(transactionCreateDTOList).flatMap(this::create);

		/*transactionCreateDTOList.forEach(transactionCreateDTO -> {
			Transaction transaction = modelMapper.map(transactionCreateDTO, Transaction.class);
			transactionList.add(transaction);
		});

		log.debug("Created transactionList object: " + transactionList);

		return transactionRepository.saveAll(transactionList)
				.flatMap(transaction1 -> Mono.just(modelMapper.map(transaction1, TransactionResponse.class)))
				.flatMap(transactionResponse -> productService.get(transactionResponse.getProductId())
						.flatMap(productResponse -> {
							transactionResponse.setProduct(productResponse);

							return Mono.just(transactionResponse);
						}));*/
	}

	public Flux<TransactionResponse> preview(List<TransactionCreateDTO> transactionCreateDTOList) {
		log.info("Transaction preview: " + transactionCreateDTOList.toString());

		List<TransactionResponse> transactionResponseList = new ArrayList<>();

		transactionCreateDTOList.forEach(transactionCreateDTO -> {
			TransactionResponse transactionResponse = modelMapper.map(transactionCreateDTO, TransactionResponse.class);
			transactionResponseList.add(transactionResponse);
		});

		log.info("Preview transactionResponseList object: " + transactionResponseList);

		return Flux.fromIterable(transactionResponseList)
				.flatMap(transactionResponse -> productService.get(transactionResponse.getProductId())
						.flatMap(productResponse -> {
							transactionResponse.setProduct(productResponse);

							log.info("Preview transactionResponse object: " + transactionResponse);
							log.info("Preview productResponse object: " + productResponse);

							return Mono.just(transactionResponse);
						}));
	}

	public Mono<TransactionResponse> update(UUID id, TransactionCreateDTO transactionCreateDTO) {
		log.info("Transaction updating: " + transactionCreateDTO.toString());

		return this.getObject(id)
				.map(Optional::of)
				.switchIfEmpty(Mono.error(new RuntimeException("Transaction not found")))
				.flatMap(optionalTransaction -> {
					if (optionalTransaction.isPresent()) {
						Transaction updatedTransaction = this.getUpdatedTransaction(optionalTransaction.get(), transactionCreateDTO);

						return transactionRepository.save(updatedTransaction);
					}
					return Mono.empty();
				})
				.flatMap(transaction -> Mono.just(modelMapper.map(transaction, TransactionResponse.class)))
				.flatMap(transactionResponse -> productService.get(transactionResponse.getProductId())
						.flatMap(productResponse -> {
							transactionResponse.setProduct(productResponse);

							return Mono.just(transactionResponse);
						}));
	}

	private Transaction getUpdatedTransaction(Transaction transaction, TransactionCreateDTO transactionCreateDTO) {
		if (transactionCreateDTO.getReceiptNumber() != null) {
			transaction.setReceiptNumber(transactionCreateDTO.getReceiptNumber());
		}

		if (transactionCreateDTO.getType() != null) {
			transaction.setType(transactionCreateDTO.getType());
		}

		if (transactionCreateDTO.getAmount() != null) {
			transaction.setAmount(transactionCreateDTO.getAmount());
		}

		if (transactionCreateDTO.getDescription() != null) {
			transaction.setDescription(transactionCreateDTO.getDescription());
		}

		if (transactionCreateDTO.getIssueDate() != null) {
			transaction.setIssueDate(transactionCreateDTO.getIssueDate());
		}

		if (transactionCreateDTO.getContractId() != null) {
			transaction.setContractId(transactionCreateDTO.getContractId());
		}

		if (transactionCreateDTO.getProductId() != null) {
			transaction.setProductId(transactionCreateDTO.getProductId());
		}

		log.info("Updated transaction object: " + transaction);

		return transaction;
	}

	public Mono<Void> deleteById(UUID id) {
		log.info("Transaction deleting: " + id);

		return transactionRepository.deleteById(id);
	}

	public void isTransactionValid(List<TransactionResponse> transactionResponseList, TransportType type, UUID productId, Double amount) {
		if (type.equals(TransportType.INBOUND)) {
			if (!transactionResponseList.stream().map(TransactionResponse::getProductId).toList().contains(productId)) {
				throw new ProductNotFoundForContractException("Product not found for contract");
			}

			transactionResponseList.forEach(transactionResponseInContract -> {
				if (transactionResponseInContract.getProductId().equals(productId)) {
					if (transactionResponseInContract.getAmount() < amount) {
						throw new WrongAmountTransactionException("Product amount is too much for contract");
					}
				}
			});
		}
	}

	public Mono<Map<UUID, Double>> calculateTransactions(UUID contractId, LocalDateTime startTime, LocalDateTime endTime) {
		AtomicReference<Map<UUID, Double>> transactionsWithAmount = new AtomicReference<>(new HashMap<>());
		return this.transactionRepository
				.findAllByContractId(contractId)
				.filter(transaction -> transaction.getIssueDate().isAfter(startTime))
				.concatMap(transaction -> {
					log.info("Transaction: " + transaction.toString());
					return productService.get(transaction.getProductId())
							.map(productResponse -> {
								log.info("ProductResponse: " + productResponse.toString());
								if (productResponse.getFactor().compareTo(1.0) > 0) {
									double amount = transaction.getAmount() * productResponse.getFactor(); // amount * factor
									log.info("Amount1: " + amount);

									double days = ChronoUnit.DAYS.between(transaction.getIssueDate(), endTime);
									log.info("days: " + days);

									amount = amount * days;
									log.info("Amount2: " + amount);

									if (transaction.getType().equals(TransportType.INBOUND)) {
										amount = amount * -1;
									}
									log.info("Amount3: " + amount);

									double totalAmount = transactionsWithAmount.get().get(transaction.getProductId()) == null ? 0 : transactionsWithAmount.get().get(transaction.getProductId());
									transactionsWithAmount.get().put(transaction.getProductId(), amount + totalAmount);

									log.info("transactionsWithAmount.get(): " + transactionsWithAmount.get());

									return transactionsWithAmount.get();
								}
								return transactionsWithAmount.get();
							});
				})
				.collect(Collectors.toList())
				.flatMap(transactions -> Mono.just(transactionsWithAmount.get()));
	}
}
