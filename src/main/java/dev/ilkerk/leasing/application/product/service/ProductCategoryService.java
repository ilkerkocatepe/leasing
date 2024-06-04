package dev.ilkerk.leasing.application.product.service;

import dev.ilkerk.leasing.application.product.dto.request.productCategory.ProductCategoryCreateDTO;
import dev.ilkerk.leasing.application.product.dto.request.productCategory.ProductCategoryFindDTO;
import dev.ilkerk.leasing.application.product.dto.response.ProductCategoryResponse;
import dev.ilkerk.leasing.domain.product.entity.ProductCategory;
import dev.ilkerk.leasing.domain.product.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductCategoryService {
	private final ProductCategoryRepository productCategoryRepository;
	private final ModelMapper modelMapper;

	@Value("${aws.product-category-image-folder}")
	private String bucketImageFolder;

	public Mono<ProductCategoryResponse> get(UUID id) {
		return productCategoryRepository.findById(id).flatMap(productCategory -> {
			ProductCategoryResponse productCategoryResponse = modelMapper.map(productCategory, ProductCategoryResponse.class);

			if (productCategoryResponse.getImage() != null) {
				productCategoryResponse.setImageUrl(bucketImageFolder + productCategoryResponse.getImage());
			}

			return Mono.just(productCategoryResponse);
		});
	}

	public Mono<ProductCategory> getObject(UUID id) {
		return productCategoryRepository.findById(id);
	}

	public Flux<ProductCategoryResponse> getAllByCriteria(ProductCategoryFindDTO productCategoryFindDTO) {
		ProductCategory productCategory = modelMapper.map(productCategoryFindDTO, ProductCategory.class);

		Example<ProductCategory> productCategoryExample = Example.of(productCategory, ProductCategoryFindDTO.getExampleMatcher());

		return productCategoryRepository.findAll(productCategoryExample).flatMap(productCategory1 -> {
			ProductCategoryResponse productCategoryResponse = modelMapper.map(productCategory1, ProductCategoryResponse.class);

			if (productCategoryResponse.getImage() != null) {
				productCategoryResponse.setImageUrl(bucketImageFolder + productCategoryResponse.getImage());
			}

			return Mono.just(productCategoryResponse);
		});
	}

	public Mono<ProductCategoryResponse> create(ProductCategoryCreateDTO productCategoryCreateDTO) {
        log.info("ProductCategory creating: {}", productCategoryCreateDTO.toString());

		ProductCategory productCategory = modelMapper.map(productCategoryCreateDTO, ProductCategory.class);

        log.debug("Created productCategory object: {}", productCategory);

		return productCategoryRepository.save(productCategory).flatMap(productCategory1 -> {
			ProductCategoryResponse productCategoryResponse = modelMapper.map(productCategory1, ProductCategoryResponse.class);

			if (productCategoryResponse.getImage() != null) {
				productCategoryResponse.setImageUrl(bucketImageFolder + productCategoryResponse.getImage());
			}

			return Mono.just(productCategoryResponse);
		});
	}

	public Mono<ProductCategoryResponse> update(UUID id, ProductCategoryCreateDTO productCategoryCreateDTO) {
        log.info("ProductCategory updating: {}", productCategoryCreateDTO.toString());

		return this.getObject(id)
				.map(Optional::of)
				.switchIfEmpty(Mono.error(new RuntimeException("ProductCategory not found")))
				.flatMap(optionalProductCategory -> {
					if (optionalProductCategory.isPresent()) {
						ProductCategory updatedProductCategory = this.getUpdatedProductCategory(optionalProductCategory.get(), productCategoryCreateDTO);

						return productCategoryRepository.save(updatedProductCategory);
					}
					return Mono.empty();
				})
				.flatMap(productCategory1 -> {
					ProductCategoryResponse productCategoryResponse = modelMapper.map(productCategory1, ProductCategoryResponse.class);

					if (productCategoryResponse.getImage() != null) {
						productCategoryResponse.setImageUrl(bucketImageFolder + productCategoryResponse.getImage());
					}

					return Mono.just(productCategoryResponse);
				});
	}

	private ProductCategory getUpdatedProductCategory(ProductCategory productCategory, ProductCategoryCreateDTO productCategoryCreateDTO) {
		if (productCategoryCreateDTO.getName() != null) {
			productCategory.setName(productCategoryCreateDTO.getName());
		}

		if (productCategoryCreateDTO.getImage() != null) {
			productCategory.setImage(productCategoryCreateDTO.getImage());
		}

        log.info("Updated productHistory object: {}", productCategory);

		return productCategory;
	}

	public Mono<Void> deleteById(UUID id) {
        log.info("ProductCategory deleting: {}", id);

		return productCategoryRepository.deleteById(id);
	}
}
