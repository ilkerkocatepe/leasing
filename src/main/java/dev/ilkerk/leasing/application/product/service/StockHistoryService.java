package dev.ilkerk.leasing.application.product.service;

import dev.ilkerk.leasing.application.product.dto.response.StockHistoryResponse;
import dev.ilkerk.leasing.domain.product.entity.StockHistory;
import dev.ilkerk.leasing.domain.product.repository.StockHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockHistoryService implements HistoryService<StockHistory> {
	private final StockHistoryRepository stockHistoryRepository;
	private final ModelMapper modelMapper;

	@Override
	public Mono<Void> updated(StockHistory stockHistory) {
		return stockHistoryRepository.save(stockHistory).then();
	}

	public Mono<StockHistory> getObjectById(UUID id) {
		return stockHistoryRepository.findById(id);
	}

	public Flux<StockHistoryResponse> getAllByStockId(UUID stockId) {
		return stockHistoryRepository.findAllByStockIdOrderByCreatedAtDesc(stockId)
				.flatMap(stockHistory -> Mono.just(modelMapper.map(stockHistory, StockHistoryResponse.class)));
	}
}
