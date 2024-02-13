package dev.ilkerk.leasing.application.contract.service;

import dev.ilkerk.leasing.application.contract.dto.request.transport.TransportCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.transport.TransportFindDTO;
import dev.ilkerk.leasing.domain.contract.entity.Transport;
import dev.ilkerk.leasing.domain.contract.repository.TransportRepository;
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
public class TransportService {
	private final TransportRepository transportRepository;
	private final ModelMapper modelMapper;

	public Mono<Transport> get(UUID id) {
		return transportRepository.findById(id);
	}

	public Flux<Transport> getAllByCriteria(TransportFindDTO transportFindDTO) {
		Transport transport = modelMapper.map(transportFindDTO, Transport.class);

		Example<Transport> transportExample = Example.of(transport, TransportFindDTO.getExampleMatcher());

		return transportRepository.findAll(transportExample);
	}

	public Mono<Transport> create(TransportCreateDTO transportCreateDTO) {
		log.info("Transport creating: " + transportCreateDTO.toString());

		Transport transport = modelMapper.map(transportCreateDTO, Transport.class);

		log.debug("Created transport object: " + transport);

		return transportRepository.save(transport);
	}

	public Mono<Transport> update(UUID id, TransportCreateDTO transportCreateDTO) {
		log.info("Transport updating: " + transportCreateDTO.toString());

		return this.get(id).map(Optional::of).defaultIfEmpty(Optional.empty())
						.flatMap(optionalTransport -> {
							if (optionalTransport.isPresent()) {
								Transport transport = modelMapper.map(transportCreateDTO, Transport.class);

								log.info("Updated transport object: " + transport);

								return transportRepository.save(transport);
							}
							return Mono.empty();
						});
	}

	public Mono<Void> deleteById(UUID id) {
		log.info("Transport deleting: " + id);

		return transportRepository.deleteById(id);
	}
}
