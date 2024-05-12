package com.retailcloud.empmgt.advice.response;

import lombok.Builder;

@Builder
public record ErrorResponse(String message) {
}
