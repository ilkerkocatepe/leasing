package dev.ilkerk.leasing.application.product.event;

import dev.ilkerk.leasing.domain.product.entity.Product;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ProductCreatedEvent extends ApplicationEvent {
	@Getter
	private Product product;
	@Getter
	private Double amount;

	public ProductCreatedEvent(Object source, Product product, Double amount) {
		super(source);
		this.product = product;
		this.amount = amount;
	}

}
