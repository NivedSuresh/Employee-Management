package com.retailcloud.empmgt.model.payload;

import lombok.Builder;

@Builder
public record Message(String message) {
}
