package com.vanakkam.skillroute.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleRequest {

    @NotBlank(message = "Module title cannot be blank")
    private String title;

    private String description;
    private String contentUrl;

    @NotNull(message = "Sequence order is mandatory")
    private Integer sequenceOrder;

    private String difficultyTag;
}