package org.piotrowski.cardureadoo.web.dto.expansion;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatchExpansionRequest(String name) { }
