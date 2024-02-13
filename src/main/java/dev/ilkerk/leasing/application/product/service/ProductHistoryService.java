package dev.ilkerk.leasing.application.product.service;

import dev.ilkerk.leasing.domain.product.entity.ProductHistory;
import dev.ilkerk.leasing.domain.product.repository.ProductHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductHistoryService implements HistoryService<ProductHistory> {
	private final ProductHistoryRepository productHistoryRepository;

	@Override
	public Mono<Void> updated(ProductHistory productHistory) {
		return productHistoryRepository.save(productHistory).then();
	}
}
