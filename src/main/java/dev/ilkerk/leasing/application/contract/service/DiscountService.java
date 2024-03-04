package dev.ilkerk.leasing.application.contract.service;

import dev.ilkerk.leasing.application.contract.dto.request.contract.DiscountCreate;
import dev.ilkerk.leasing.application.contract.dto.request.contract.DiscountFind;
import dev.ilkerk.leasing.application.contract.dto.response.DiscountResponse;
import dev.ilkerk.leasing.domain.contract.entity.Discount;
import dev.ilkerk.leasing.domain.contract.repository.DiscountRepository;
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
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final ModelMapper modelMapper;

    public Mono<DiscountResponse> get(UUID id) {
        return discountRepository.findById(id)
                .flatMap(discount -> Mono.just(modelMapper.map(discount, DiscountResponse.class)));
    }

    public Mono<Discount> getObject(UUID id) {
        return discountRepository.findById(id);
    }

    public Flux<DiscountResponse> getAllByCriteria(DiscountFind discountFind) {
        Discount discount = modelMapper.map(discountFind, Discount.class);

        Example<Discount> discountExample = Example.of(discount, DiscountFind.getExampleMatcher());

        return discountRepository.findAll(discountExample)
                .flatMap(discount1 -> Mono.just(modelMapper.map(discount1, DiscountResponse.class)));
    }

    public Mono<DiscountResponse> create(DiscountCreate discountCreate) {
        if (discountCreate.getAmount() == null || discountCreate.getAmount() <= 0) {
            return Mono.error(new RuntimeException("Discount amount must be greater than 0"));
        }

        log.info("Discount creating: " + discountCreate.toString());

        Discount discount = modelMapper.map(discountCreate, Discount.class);

        log.debug("Created discount object: " + discount);

        return discountRepository.save(discount)
                .flatMap(discount1 -> Mono.just(modelMapper.map(discount1, DiscountResponse.class)));
    }

    public Mono<DiscountResponse> update(UUID id, DiscountCreate discountCreate) {
        log.info("Discount updating: " + discountCreate.toString());

        return this.getObject(id)
                .map(Optional::of)
                .switchIfEmpty(Mono.error(new RuntimeException("Discount not found")))
                .flatMap(optionalDiscount -> {
                    if (optionalDiscount.isPresent()) {
                        Discount updatedDiscount = this.getUpdatedDiscount(optionalDiscount.get(), discountCreate);

                        return discountRepository.save(updatedDiscount);
                    }
                    return Mono.empty();
                })
                .flatMap(discount1 -> Mono.just(modelMapper.map(discount1, DiscountResponse.class)));
    }

    public Discount getUpdatedDiscount(Discount discount, DiscountCreate discountCreate) {

        log.info("Updated discount object: " + discount);

        return discount;
    }

    public Mono<Void> deleteById(UUID id) {
        log.info("Discount deleting: " + id);

        return discountRepository.deleteById(id);
    }
}
