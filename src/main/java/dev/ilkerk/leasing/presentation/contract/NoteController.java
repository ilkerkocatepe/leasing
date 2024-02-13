package dev.ilkerk.leasing.presentation.contract;

import dev.ilkerk.leasing.application.contract.dto.request.note.NoteCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.note.NoteFindDTO;
import dev.ilkerk.leasing.application.contract.dto.response.NoteResponse;
import dev.ilkerk.leasing.application.contract.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("note")
@Slf4j
@RequiredArgsConstructor
public class NoteController {
	private final NoteService noteService;

	@GetMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<NoteResponse> getById(@PathVariable UUID id) {
		try {
			return noteService.get(id);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public Flux<NoteResponse> getAll(@ModelAttribute @Valid NoteFindDTO noteFindDTO) {
		try {
			return noteService.getAllByCriteria(noteFindDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Flux.error(e);
		}
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<NoteResponse> create(@RequestBody @Valid NoteCreateDTO noteCreateDTO, Authentication authentication) {
		try {
			return noteService.create(noteCreateDTO, authentication, null);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@PutMapping("{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<NoteResponse> update(@PathVariable UUID id, @RequestBody @Valid NoteCreateDTO noteCreateDTO) {
		try {
			return noteService.update(id, noteCreateDTO);
		} catch (Exception e) {
			log.error(e.getMessage());

			return Mono.error(e);
		}
	}

	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> delete(@PathVariable UUID id) {
		return noteService.deleteById(id);
	}
}
