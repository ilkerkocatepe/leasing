package dev.ilkerk.leasing.application.customer.service;

import dev.ilkerk.leasing.application.customer.dto.request.customerPreference.CustomerPreferenceCreateDTO;
import dev.ilkerk.leasing.application.customer.dto.request.customerPreference.CustomerPreferenceFindDTO;
import dev.ilkerk.leasing.application.customer.dto.response.CustomerPreferenceResponse;
import dev.ilkerk.leasing.domain.customer.entity.CustomerPreference;
import dev.ilkerk.leasing.domain.customer.preference.PaymentCalculationType;
import dev.ilkerk.leasing.domain.customer.preference.PreferenceType;
import dev.ilkerk.leasing.domain.customer.repository.CustomerPreferenceRepository;
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
public class CustomerPreferenceService {
	private final CustomerPreferenceRepository customerPreferenceRepository;
	private final ModelMapper modelMapper;

	public Mono<CustomerPreferenceResponse> get(UUID id) {
		return customerPreferenceRepository.findById(id)
				.flatMap(customerPreference -> Mono.just(modelMapper.map(customerPreference, CustomerPreferenceResponse.class)));
	}

	public Mono<CustomerPreference> getObject(UUID id) {
		return customerPreferenceRepository.findById(id);
	}

	public Flux<CustomerPreferenceResponse> getAllByCriteria(CustomerPreferenceFindDTO customerPreferenceFindDTO) {
		CustomerPreference customerPreference = modelMapper.map(customerPreferenceFindDTO, CustomerPreference.class);

		Example<CustomerPreference> customerPreferenceExample = Example.of(customerPreference, CustomerPreferenceFindDTO.getExampleMatcher());

		return customerPreferenceRepository.findAll(customerPreferenceExample)
				.flatMap(customerPreference1 -> Mono.just(modelMapper.map(customerPreference1, CustomerPreferenceResponse.class)));
	}

	public Flux<CustomerPreferenceResponse> getAllByCustomerId(UUID customerId) {
		return customerPreferenceRepository.findAllByCustomerId(customerId)
				.flatMap(customerPreference -> Mono.just(modelMapper.map(customerPreference, CustomerPreferenceResponse.class)));
	}

	public Mono<CustomerPreferenceResponse> create(CustomerPreferenceCreateDTO customerPreferenceCreateDTO) {
		log.info("PreferenceType creating: " + customerPreferenceCreateDTO.toString());

		CustomerPreference customerPreference = modelMapper.map(customerPreferenceCreateDTO, CustomerPreference.class);

		log.debug("Created customerPreference object: " + customerPreference);

		return customerPreferenceRepository.save(customerPreference)
				.flatMap(customerPreference1 -> Mono.just(modelMapper.map(customerPreference1, CustomerPreferenceResponse.class)));
	}

	public Mono<CustomerPreferenceResponse> update(UUID id, CustomerPreferenceCreateDTO customerPreferenceCreateDTO) {
		log.info("PreferenceType updating: " + customerPreferenceCreateDTO.toString());

		return this.getObject(id)
				.map(Optional::of)
				.switchIfEmpty(Mono.error(new Exception("CustomerPreference not found")))
				.flatMap(optionalCustomerPreference -> {
							if (optionalCustomerPreference.isPresent()) {
								CustomerPreference updatedCustomerPreference = this.getUpdatedCustomerPreference(optionalCustomerPreference.get(), customerPreferenceCreateDTO);

								log.info("Updated customerPreference object: " + updatedCustomerPreference);

								return customerPreferenceRepository.save(updatedCustomerPreference);
							}
							return Mono.empty();
						})
				.flatMap(customerPreference1 -> Mono.just(modelMapper.map(customerPreference1, CustomerPreferenceResponse.class)));
	}

	private CustomerPreference getUpdatedCustomerPreference(CustomerPreference customerPreference, CustomerPreferenceCreateDTO customerPreferenceCreateDTO) {
		if (customerPreferenceCreateDTO.getName() != null) {
			customerPreference.setName(customerPreferenceCreateDTO.getName());
		}
		if (customerPreferenceCreateDTO.getValue() != null) {
			customerPreference.setValue(customerPreferenceCreateDTO.getValue());
		}
		if (customerPreferenceCreateDTO.getDescription() != null) {
			customerPreference.setDescription(customerPreferenceCreateDTO.getDescription());
		}
		return customerPreference;
	}

	public Mono<Void> deleteById(UUID id) {
		log.info("PreferenceType deleting: " + id);

		return customerPreferenceRepository.deleteById(id);
	}

	public Mono<String> getValueByKey(UUID customerId, String key) {
		log.info("PreferenceType getting value by customerId {} and key: {}", customerId, key);

		return customerPreferenceRepository.findByCustomerIdAndNameOrderByIdDesc(customerId, key).next().map(CustomerPreference::getValue);
	}

	public Mono<PaymentCalculationType> getPaymentCalculationType(UUID sellerCustomerId) {
		return this.getValueByKey(sellerCustomerId, PreferenceType.PaymentCalculationType.getKeyName()).map(PaymentCalculationType::valueOf);
	}

	public Mono<Double> getSpecialAreaPrice(UUID sellerCustomerId) {
		return this.getValueByKey(sellerCustomerId, PreferenceType.SpecialAreaPrice.getKeyName()).map(Double::valueOf);
	}
}
