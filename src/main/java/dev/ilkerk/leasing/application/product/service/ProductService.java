package dev.ilkerk.leasing.application.product.service;

import dev.ilkerk.leasing.application.product.dto.request.product.ProductCreateDTO;
import dev.ilkerk.leasing.application.product.dto.request.product.ProductFindDTO;
import dev.ilkerk.leasing.application.product.dto.response.ProductResponse;
import dev.ilkerk.leasing.application.product.dto.response.StockResponse;
import dev.ilkerk.leasing.application.product.event.ProductCreatedEvent;
import dev.ilkerk.leasing.application.user.service.UserService;
import dev.ilkerk.leasing.domain.product.entity.Product;
import dev.ilkerk.leasing.domain.product.entity.ProductHistory;
import dev.ilkerk.leasing.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final ProductHistoryService historyService;
    private final StockService stockService;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${aws.product-image-folder}")
    private String bucketImageFolder;

    public Mono<ProductResponse> get(UUID id) {
        return productRepository.findById(id)
                .flatMap(product -> {
                    ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);

                    if (productResponse.getImage() != null) {
                        productResponse.setImageUrl(bucketImageFolder + productResponse.getImage());
                    }

                    return Mono.just(productResponse).flatMap(productResponse1 -> stockService.getByProductId(product.getId()).defaultIfEmpty(new StockResponse()).flatMap(stockResponse -> {
                        productResponse1.setStock(stockResponse);

                        return Mono.just(productResponse1);
                    }));
                });
    }

    public Mono<Product> getObject(UUID id) {
        return productRepository.findById(id);
    }

    public Flux<ProductResponse> getAllByCriteria(ProductFindDTO productFindDTO) {
        Product product = modelMapper.map(productFindDTO, Product.class);

        Example<Product> productExample = Example.of(product, ProductFindDTO.getExampleMatcher());

        return productRepository.findAll(productExample)
                .flatMap(product1 -> {
                    ProductResponse productResponse = modelMapper.map(product1, ProductResponse.class);

                    if (productResponse.getImage() != null) {
                        productResponse.setImageUrl(bucketImageFolder + productResponse.getImage());
                    }

                    Mono<StockResponse> stock = stockService.getByProductId(product1.getId())
                            .defaultIfEmpty(new StockResponse());

                    return Flux.combineLatest(stock, Flux.just(productResponse), (stockResponse, productResponse1) -> {
                        productResponse1.setStock(stockResponse);

                        return productResponse1;
                    });
                });
    }

    public Mono<ProductResponse> create(ProductCreateDTO productCreateDTO, Authentication authentication) {
        log.info("Product creating: " + productCreateDTO.toString());

        String customerId = userService.getUserIdAndCustomerId(authentication).get("customerId");

        Product product = modelMapper.map(productCreateDTO, Product.class);
		product.setCustomerId(UUID.fromString(customerId));

        log.debug("Created product object: " + product);

        return productRepository.save(product)
                .doOnSuccess(p -> eventPublisher.publishEvent(new ProductCreatedEvent(this, p, productCreateDTO.getStock())))
                .flatMap(product1 -> {
                    ProductResponse productResponse = modelMapper.map(product1, ProductResponse.class);

                    if (productResponse.getImage() != null) {
                        productResponse.setImageUrl(bucketImageFolder + productResponse.getImage());
                    }
                    return Mono.just(productResponse);
                });
    }

    public Mono<ProductResponse> update(UUID id, ProductCreateDTO productCreateDTO) {
        log.info("Product updating: " + productCreateDTO.toString());

        return this.getObject(id)
                .map(Optional::of)
                .switchIfEmpty(Mono.error(new Exception("Product not found")))
                .flatMap(optionalProduct -> {
                    if (optionalProduct.isPresent()) {
                        Product updatedProduct = this.getUpdatedProduct(optionalProduct.get(), productCreateDTO);

                        historyService.updated(modelMapper.map(productCreateDTO, ProductHistory.class)).toFuture();

                        return productRepository.save(updatedProduct);
                    }
                    return Mono.empty();
                })
                .flatMap(product -> {
                    ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);

                    if (productResponse.getImage() != null) {
                        productResponse.setImageUrl(bucketImageFolder + productResponse.getImage());
                    }

                    return Mono.just(productResponse).flatMap(productResponse1 -> stockService.getByProductId(product.getId()).flatMap(stockResponse -> {
                        productResponse1.setStock(stockResponse);

                        return Mono.just(productResponse1);
                    }));
                });
    }

    private Product getUpdatedProduct(Product product, ProductCreateDTO productCreateDTO) {
        if (productCreateDTO.getName() != null) {
            product.setName(productCreateDTO.getName());
        }

        if (productCreateDTO.getImage() != null) {
            product.setImage(productCreateDTO.getImage());
        }

        if (productCreateDTO.getPrice() != null) {
            product.setPrice(productCreateDTO.getPrice());
        }

        if (productCreateDTO.getFactor() != null) {
            product.setFactor(productCreateDTO.getFactor());
        }

        if (productCreateDTO.getUnit() != null) {
            product.setUnit(productCreateDTO.getUnit());
        }

        if (productCreateDTO.getCategoryId() != null) {
            product.setCategoryId(productCreateDTO.getCategoryId());
        }

        log.info("Updated product object: " + product);

        return product;
    }

    public Mono<Void> deleteById(UUID id) {
        log.info("Product deleting: " + id);

        return productRepository.deleteById(id);
    }
}
