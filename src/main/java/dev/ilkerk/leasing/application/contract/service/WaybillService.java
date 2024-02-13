package dev.ilkerk.leasing.application.contract.service;

import dev.ilkerk.leasing.application.contract.dto.request.waybill.WaybillCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.waybill.WaybillFindDTO;
import dev.ilkerk.leasing.domain.contract.entity.Waybill;
import dev.ilkerk.leasing.domain.contract.repository.WaybillRepository;
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
public class WaybillService {
	private final WaybillRepository waybillRepository;
	private final ModelMapper modelMapper;

	public Mono<Waybill> get(UUID id) {
		return waybillRepository.findById(id);
	}

	public Flux<Waybill> getAllByCriteria(WaybillFindDTO waybillFindDTO) {
		Waybill waybill = modelMapper.map(waybillFindDTO, Waybill.class);

		Example<Waybill> waybillExample = Example.of(waybill, WaybillFindDTO.getExampleMatcher());

		return waybillRepository.findAll(waybillExample);
	}

	public Mono<Waybill> create(WaybillCreateDTO waybillCreateDTO) {
		log.info("Waybill creating: " + waybillCreateDTO.toString());

		Waybill waybill = modelMapper.map(waybillCreateDTO, Waybill.class);

		log.debug("Created waybill object: " + waybill);

		return waybillRepository.save(waybill);
	}

	public Mono<Waybill> update(UUID id, WaybillCreateDTO waybillCreateDTO) {
		log.info("Waybill updating: " + waybillCreateDTO.toString());

		return this.get(id).map(Optional::of).defaultIfEmpty(Optional.empty())
						.flatMap(optionalWaybill -> {
							if (optionalWaybill.isPresent()) {
								Waybill waybill = modelMapper.map(waybillCreateDTO, Waybill.class);

								log.info("Updated waybill object: " + waybill);

								return waybillRepository.save(waybill);
							}
							return Mono.empty();
						});
	}

	public Mono<Void> deleteById(UUID id) {
		log.info("Waybill deleting: " + id);

		return waybillRepository.deleteById(id);
	}
}
