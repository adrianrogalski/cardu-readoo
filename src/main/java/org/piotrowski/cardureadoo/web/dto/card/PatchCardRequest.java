package org.piotrowski.cardureadoo.web.dto.card;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatchCardRequest(String name, String rarity) { }
