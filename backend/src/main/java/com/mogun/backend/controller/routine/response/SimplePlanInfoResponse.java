package com.mogun.backend.controller.routine.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SimplePlanInfoResponse {

    @JsonProperty("key")
    private int execKey;

    @JsonProperty("name")
    private String execName;

    @JsonProperty("sensing_part")
    private List<String> musclePart;
}