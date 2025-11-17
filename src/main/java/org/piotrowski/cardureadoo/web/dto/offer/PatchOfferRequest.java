package org.piotrowski.cardureadoo.web.dto.offer;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PatchOfferRequest(BigDecimal amount, String currency, Instant listedAt) { }
