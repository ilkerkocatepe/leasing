package dev.ilkerk.leasing.application.product.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface HistoryService<T> {
	Mono<Void> updated(T t);
}
