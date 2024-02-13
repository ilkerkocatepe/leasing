package dev.ilkerk.leasing.application.contract.service;

import dev.ilkerk.leasing.application.contract.dto.request.note.NoteCreateDTO;
import dev.ilkerk.leasing.application.contract.dto.request.note.NoteFindDTO;
import dev.ilkerk.leasing.application.contract.dto.response.ContractResponse;
import dev.ilkerk.leasing.application.contract.dto.response.NoteResponse;
import dev.ilkerk.leasing.application.user.service.UserService;
import dev.ilkerk.leasing.domain.contract.entity.Note;
import dev.ilkerk.leasing.domain.contract.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoteService {
	private final NoteRepository noteRepository;
	private final ModelMapper modelMapper;
	private final ContractNotesService contractNotesService;
	private final UserService userService;

	public Mono<NoteResponse> get(UUID id) {
		return noteRepository.findById(id).flatMap(note -> Mono.just(modelMapper.map(note, NoteResponse.class)));
	}

	public Mono<Note> getObject(UUID id) {
		return noteRepository.findById(id);
	}

	public Flux<NoteResponse> getAllByCriteria(NoteFindDTO noteFindDTO) {
		Note note = modelMapper.map(noteFindDTO, Note.class);

		Example<Note> noteExample = Example.of(note, NoteFindDTO.getExampleMatcher());

		return noteRepository.findAll(noteExample).flatMap(note1 -> Mono.just(modelMapper.map(note1, NoteResponse.class)));
	}

	public Mono<NoteResponse> create(NoteCreateDTO noteCreateDTO, Authentication authentication, Object relatedObject) {
		log.info("Note creating: " + noteCreateDTO.toString());

		Note note = modelMapper.map(noteCreateDTO, Note.class);
		note.setUserId(UUID.fromString(userService.getUserIdAndCustomerId(authentication).get("userId")));

		log.debug("Created note object: " + note);

		return noteRepository.save(note)
				.flatMap(note1 -> {
					if (relatedObject instanceof ContractResponse) {
						contractNotesService.create(((ContractResponse) relatedObject).getId(), note1.getId()).toFuture();
					}

					return Mono.just(note1);
				})
				.flatMap(note1 -> Mono.just(modelMapper.map(note1, NoteResponse.class)));
	}

	public Flux<NoteResponse> createAll(List<NoteCreateDTO> noteCreateDTOList, UUID userId, Object relatedObject) {
		log.info("Note creating: " + noteCreateDTOList.toString());

		List<Note> noteList = new ArrayList<>();

		noteCreateDTOList.forEach(noteCreateDTO -> {
			Note note = modelMapper.map(noteCreateDTO, Note.class);
			note.setUserId(userId);
			noteList.add(note);
		});

		log.debug("Created note object: " + noteList);

		return noteRepository.saveAll(noteList)
				.flatMap(note1 -> {
					if (relatedObject instanceof ContractResponse) {
                        contractNotesService.create(((ContractResponse) relatedObject).getId(), note1.getId()).toFuture();
					}

					return Mono.just(note1);
				})
				.flatMap(note1 -> Mono.just(modelMapper.map(note1, NoteResponse.class)));
	}

	public Mono<NoteResponse> update(UUID id, NoteCreateDTO noteCreateDTO) {
		log.info("Note updating: " + noteCreateDTO.toString());

		return this.getObject(id)
				.map(Optional::of)
				.switchIfEmpty(Mono.error(new RuntimeException("Note not found")))
				.flatMap(optionalNote -> {
					if (optionalNote.isPresent()) {
						Note updatedNote = modelMapper.map(noteCreateDTO, Note.class);

						return noteRepository.save(updatedNote);
					}
					return Mono.empty();
				})
				.flatMap(note -> Mono.just(modelMapper.map(note, NoteResponse.class)));
	}

	private Note getUpdatedNoteObject(Note note, NoteCreateDTO noteCreateDTO) {
		if (noteCreateDTO.getText() != null) {
			note.setText(noteCreateDTO.getText());
		}

		log.info("Updated note object: " + note);

		return note;
	}

	public Mono<Void> deleteById(UUID id) {
		log.info("Note deleting: " + id);

		return noteRepository.deleteById(id);
	}

	public Flux<NoteResponse> getAllByContractId(UUID contractId) {
		return contractNotesService.getAllByContractId(contractId)
				.flatMap(contractNotes -> this.getObject(contractNotes.getNoteId()))
				.flatMap(note -> Mono.just(modelMapper.map(note, NoteResponse.class)));
	}
}
