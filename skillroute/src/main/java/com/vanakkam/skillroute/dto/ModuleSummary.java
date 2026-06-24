package com.vanakkam.skillroute.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModuleSummary {
    private Long id;
    private String title;
    private String difficultyTag;
    private Integer sequenceOrder;
}