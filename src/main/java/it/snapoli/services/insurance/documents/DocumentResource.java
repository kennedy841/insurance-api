package it.snapoli.services.insurance.documents;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RequestMapping("/documents")
@RestController
public class DocumentResource {

    private final DocumentsRepository documentsRepository;

    @GetMapping
    public Page<DocumentsDto> findAll(@RequestParam(name = "reference", defaultValue = "1") int id,
                                      @RequestParam(name = "referenceType", defaultValue = "1") String type,
                                      @RequestParam(name = "page", defaultValue = "1") int page,
                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        if ("insurance".equalsIgnoreCase(type)) {
            return documentsRepository.findAllByInsuranceId(id, PageRequest.of(page - 1, size)).map(documentEntity -> Mappers.getMapper(DocumentsDto.DocumentMapper.class).from(documentEntity));
        }
        if ("customer".equalsIgnoreCase(type)) {
            return documentsRepository.findAllByCustomerId(id, PageRequest.of(page - 1, size)).map(documentEntity -> Mappers.getMapper(DocumentsDto.DocumentMapper.class).from(documentEntity));
        }
        return Page.empty();
    }

    @PostMapping
    public FullDocumentsDto create(@Valid DocumentEntity documentEntity) {
        return convertToDto(documentsRepository.save(documentEntity));
    }

    @PutMapping
    public FullDocumentsDto update(@Valid DocumentEntity documentEntity) {
        return convertToDto(documentsRepository.save(documentEntity));
    }

    private static FullDocumentsDto convertToDto(DocumentEntity save) {
        FullDocumentsDto from = Mappers.getMapper(FullDocumentsDto.ContentDocumentsDtoMapper.class).from(save);
        from.setDocumentParts(getDocumentPartEntities(save).stream().map(v -> Mappers.getMapper(FullDocumentsDto.DocumentContentDto.DocumentContentDtoMapper.class).from(v)).collect(Collectors.toList()));
        return from;
    }

    private static List<DocumentPartEntity> getDocumentPartEntities(DocumentEntity save) {
        return Optional.ofNullable(save.getDocumentParts()).orElse(Collections.emptyList());
    }

    @GetMapping("/{id}")
    public FullDocumentsDto get(@PathVariable int id) {
        return convertToDto(documentsRepository.findById(id).orElseThrow());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        documentsRepository.deleteById(id);
    }


}
