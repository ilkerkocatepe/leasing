package dev.ilkerk.leasing.application.customer.service;

import dev.ilkerk.leasing.application.customer.dto.request.address.AddressCreateDTO;
import dev.ilkerk.leasing.application.customer.dto.request.address.AddressFindDTO;
import dev.ilkerk.leasing.application.customer.dto.response.AddressResponse;
import dev.ilkerk.leasing.domain.customer.entity.Address;
import dev.ilkerk.leasing.domain.customer.repository.AddressRepository;
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
public class AddressService {
	private final AddressRepository addressRepository;
	private final ModelMapper modelMapper;

	public Mono<AddressResponse> get(UUID id) {
		return addressRepository.findById(id)
				.flatMap(address -> Mono.just(modelMapper.map(address, AddressResponse.class)));
	}

	private Mono<Address> getObject(UUID id) {
		return addressRepository.findById(id);
	}

	public Flux<AddressResponse> getAllByCriteria(AddressFindDTO addressFindDTO) {
		Address address = modelMapper.map(addressFindDTO, Address.class);

		Example<Address> addressExample = Example.of(address, AddressFindDTO.getExampleMatcher());

		return addressRepository.findAll(addressExample)
				.flatMap(address1 -> Mono.just(modelMapper.map(address1, AddressResponse.class)));
	}

	public Flux<AddressResponse> getAllByCustomerId(UUID customerId) {
		return addressRepository.findAllByCustomerId(customerId)
				.flatMap(address -> Mono.just(modelMapper.map(address, AddressResponse.class)));
	}

	public Mono<AddressResponse> create(AddressCreateDTO addressCreateDTO) {
		log.info("Address creating: " + addressCreateDTO.toString());

		Address address = modelMapper.map(addressCreateDTO, Address.class);

		log.debug("Created address object: " + address);

		return addressRepository.save(address).flatMap(address1 -> Mono.just(modelMapper.map(address1, AddressResponse.class)));
	}

	public Mono<AddressResponse> update(UUID id, AddressCreateDTO addressCreateDTO) {
		log.info("Address updating: " + addressCreateDTO.toString());

		return this.getObject(id)
				.map(Optional::of)
				.switchIfEmpty(Mono.error(new Exception("Address not found")))
				.flatMap(optionalAddress -> {
					if (optionalAddress.isPresent()) {
						Address updatedAddress = this.getUpdatedAddress(optionalAddress.get(), addressCreateDTO);

						return addressRepository.save(updatedAddress);
					}
					return Mono.empty();
				})
				.flatMap(address -> Mono.just(modelMapper.map(address, AddressResponse.class)));
	}

	private Address getUpdatedAddress(Address address, AddressCreateDTO addressCreateDTO) {
		if (addressCreateDTO.getName() != null) {
			address.setName(addressCreateDTO.getName());
		}

		if (addressCreateDTO.getDetails() != null) {
			address.setDetails(addressCreateDTO.getDetails());
		}

		if (addressCreateDTO.getDistrict() != null) {
			address.setDistrict(addressCreateDTO.getDistrict());
		}

		if (addressCreateDTO.getCity() != null) {
			address.setCity(addressCreateDTO.getCity());
		}

		if (addressCreateDTO.getCountry() != null) {
			address.setCountry(addressCreateDTO.getCountry());
		}

		if (addressCreateDTO.getZipcode() != null) {
			address.setZipcode(addressCreateDTO.getZipcode());
		}

		if (addressCreateDTO.getDescription() != null) {
			address.setDescription(addressCreateDTO.getDescription());
		}

		if (addressCreateDTO.getCustomerId() != null) {
			address.setCustomerId(addressCreateDTO.getCustomerId());
		}

		if (addressCreateDTO.getIsMain() != null) {
			address.setIsMain(addressCreateDTO.getIsMain());
		}

		log.info("Updated address object: " + address);

		return address;
	}

	public Mono<Void> deleteById(UUID id) {
		log.info("Address deleting: " + id);

		return addressRepository.deleteById(id);
	}
}
