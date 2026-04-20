package com.xhn.fin.skills.dto;

import com.xhn.fin.accounts.dto.SubjectCategoriesDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Skills bookkeeping context DTO.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Skills bookkeeping context")
public class SkillBookkeepingContextDTO {

    @Schema(description = "Resolved default book")
    private DefaultBookDTO defaultBook;

    @Schema(description = "Subject categories under the selected book")
    private SubjectCategoriesDTO subjectCategories;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Default book info")
    public static class DefaultBookDTO {

        @Schema(description = "Book ID")
        private Long id;

        @Schema(description = "Book name")
        private String name;

        @Schema(description = "Book description")
        private String description;

        @Schema(description = "Default currency")
        private String defaultCurrency;

        @Schema(description = "Cover URL")
        private String coverUrl;

        @Schema(description = "Whether the book comes from user config")
        private Boolean fromUserConfig;
    }
}
