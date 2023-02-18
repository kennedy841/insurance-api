package it.snapoli.services.insurance.documents;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@RequiredArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class FullDocumentsDto extends DocumentsDto{

    private List<DocumentContentDto> documentParts;

    @Data
    public static class DocumentContentDto {

        private String src;
        private String title;
        @Mapper
        public interface DocumentContentDtoMapper {
            @Mapping(source = "content", target = "src")
            @Mapping(source = "name", target = "title")
            DocumentContentDto from(DocumentPartEntity documentEntity);
        }
    }

    @Mapper
    public interface ContentDocumentsDtoMapper {
        FullDocumentsDto from(DocumentEntity documentEntity);
    }
}
