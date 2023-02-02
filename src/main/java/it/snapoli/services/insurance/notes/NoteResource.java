package it.snapoli.services.insurance.notes;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/notes")
@RequiredArgsConstructor
public class NoteResource {

    private final NoteRepository noteRepository;

    @GetMapping
    public Page<NotesEntity> getAll(@RequestParam(name = "customerId") String customerId,
                                    @RequestParam(name = "insuranceId") String insuranceId,
                                    @RequestParam(name = "page", defaultValue = "1") int page,
                                    @RequestParam(name = "size", defaultValue = "10") int size, @RequestParam(name = "q") String q){
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        if(customerId != null)
            return noteRepository.findAllByReferenceEntityTypeAndReferenceEntityOrderByIdDesc("customer",customerId,pageRequest);
        if(insuranceId != null)
            return noteRepository.findAllByReferenceEntityTypeAndReferenceEntityOrderByIdDesc("insurance",insuranceId,pageRequest);
        return noteRepository.findAll(pageRequest);

    }

    @PostMapping
    public NotesEntity save(@RequestBody @Valid RequestSave requestSave){
        return noteRepository.save(NotesEntity.builder()
                        .dateTime(requestSave.date)
                        .referenceEntity(requestSave.customerId != null ? requestSave.getCustomerId(): requestSave.insuranceId)
                        .text(requestSave.text)
                        .referenceEntityType(requestSave.customerId != null ? "customer":"insurance")
                .build());
    }

    @PutMapping("/{id}")
    public NotesEntity save(@PathVariable int id, @RequestBody @Valid RequestUpdate requestSave){
        NotesEntity byId = noteRepository.getOne(id);
        byId.setText(requestSave.getText());
        return noteRepository.save(byId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id){
        noteRepository.deleteById(id);
    }


    @Data
    public static class RequestUpdate {


        @NotNull
        private String text;
    }
    @Data
    public static class RequestSave {

        private String customerId;

        private String insuranceId;

        @NotNull
        private LocalDateTime date;

        @NotNull
        private String text;
    }
}
