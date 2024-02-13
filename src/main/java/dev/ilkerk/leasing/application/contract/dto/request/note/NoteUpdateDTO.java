package dev.ilkerk.leasing.application.contract.dto.request.note;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteUpdateDTO {
	private UUID id;
	private String text;
}
