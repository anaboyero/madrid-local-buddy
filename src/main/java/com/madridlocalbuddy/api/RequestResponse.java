package com.madridlocalbuddy.api;

import com.madridlocalbuddy.domain.ValidationError;

import java.util.List;

final class RequestResponse {

    private RequestResponse() {
    }

    record Success(boolean ok) {
    }

    record Failure(boolean ok, List<ValidationError> errors) {
    }

    record ServiceUnavailable(boolean ok, String message) {
    }
}
