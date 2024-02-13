package dev.ilkerk.leasing.application.product.service;

import dev.ilkerk.leasing.application.product.dto.request.stock.StockCreateDTO;
import dev.ilkerk.leasing.application.product.dto.request.stock.StockFindDTO;
import dev.ilkerk.leasing.application.product.dto.request.stock.StockUpdateDTO;
import dev.ilkerk.leasing.application.product.dto.response.StockResponse;
import dev.ilkerk.leasing.domain.product.entity.Stock;
import dev.ilkerk.leasing.domain.product.entity.StockAction;
import dev.ilkerk.leasing.domain.product.entity.StockHistory;
import dev.ilkerk.leasing.domain.product.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockService {
	private final StockRepository stockRepository;
	private final ModelMapper modelMapper;
	private final StockHistoryService stockHistoryService;

	public Mono<StockResponse> get(UUID id) {
		return stockRepository.findById(id)
				.flatMap(stock -> Mono.just(modelMapper.map(stock, StockResponse.class)));
	}

	public Mono<StockResponse> getByProductId(UUID productId) {
		return stockRepository.findByProductId(productId)
				.flatMap(stock -> {
					StockResponse stockResponse = modelMapper.map(stock, StockResponse.class);

					return Mono.just(stockResponse)
							.flatMap(stockResponse1 -> stockHistoryService.getAllByStockId(stock.getId()).collectList().flatMap(stockHistoryResponses -> {
								stockResponse1.setStockHistoryList(stockHistoryResponses);

								return Mono.just(stockResponse1);
							}));
				});
	}

	public Mono<Stock> getObjectByProductId(UUID productId) {
		return stockRepository.findByProductId(productId);
	}

	public Flux<StockResponse> getAllByCriteria(StockFindDTO stockFindDTO) {
		Stock stock = modelMapper.map(stockFindDTO, Stock.class);

		Example<Stock> stockExample = Example.of(stock, StockFindDTO.getExampleMatcher());

		return stockRepository.findAll(stockExample)
				.flatMap(stock1 -> Mono.just(modelMapper.map(stock1, StockResponse.class)));
	}

	public Mono<StockResponse> create(StockCreateDTO stockCreateDTO) {
		log.info("Stock creating: " + stockCreateDTO.toString());

		Stock stock = Stock.builder()
				.productId(stockCreateDTO.getProductId())
				.customerId(stockCreateDTO.getCustomerId())
				.totalAmount(stockCreateDTO.getAmount())
				.availableAmount(stockCreateDTO.getAmount())
				.build();

		log.debug("Created stock object: " + stock);

		return stockRepository.save(stock)
				.flatMap(stock1 -> Mono.just(modelMapper.map(stock1, StockResponse.class)));
	}

	public Mono<StockResponse> update(UUID productId, StockUpdateDTO stockUpdateDTO) {
		log.info("Stock updating - productId: {} - {}", productId, stockUpdateDTO.toString());

		return this.getObjectByProductId(productId)
				.map(Optional::of)
				.switchIfEmpty(Mono.error(new Exception("Stock not found")))
				.flatMap(optionalStock -> {
					if (optionalStock.isPresent()) {
						if (stockUpdateDTO.getAction().equals(StockAction.DECREASE) && optionalStock.get().getAvailableAmount() < stockUpdateDTO.getAmount()) {
							return Mono.error(new Exception("Stock amount is not enough"));
						}

						Stock updatedStock = this.getUpdatedStock(optionalStock.get(), stockUpdateDTO);

						StockHistory stockHistory = StockHistory.builder()
								.action(stockUpdateDTO.getAction())
								.amount(stockUpdateDTO.getAmount())
								.stockId(updatedStock.getId())
								.productId(updatedStock.getProductId())
								.totalAmount(updatedStock.getTotalAmount())
								.build();

						stockHistoryService.updated(stockHistory).toFuture();

						return stockRepository.save(updatedStock);
					}
					return Mono.empty();
				}).flatMap(stock -> {

					StockResponse stockResponse = modelMapper.map(stock, StockResponse.class);

					return Mono.just(stockResponse)
							.flatMap(stockResponse1 -> stockHistoryService.getAllByStockId(stock.getId()).collectList().flatMap(stockHistoryResponses -> {
								stockResponse1.setStockHistoryList(stockHistoryResponses);

								return Mono.just(stockResponse1);
							}));
				});
	}

	private Stock getUpdatedStock(Stock stock, StockUpdateDTO stockUpdateDTO) {
		if (stockUpdateDTO.getAmount() != null) {
			if (stockUpdateDTO.getAction().equals(StockAction.DECREASE)) {
				stock.setTotalAmount(stock.getTotalAmount() - stockUpdateDTO.getAmount());
				stock.setAvailableAmount(stock.getAvailableAmount() - stockUpdateDTO.getAmount());
			} else if (stockUpdateDTO.getAction().equals(StockAction.INCREASE)) {
				stock.setTotalAmount(stock.getTotalAmount() + stockUpdateDTO.getAmount());
				stock.setAvailableAmount(stock.getAvailableAmount() + stockUpdateDTO.getAmount());
			}

			log.info("Updated stock object: " + stock);
		}

		return stock;
	}

	public Mono<Void> deleteById(UUID id) {
		log.info("Stock deleting: " + id);

		return stockRepository.deleteById(id);
	}
}
