package com.example;

import am.ik.yavi.core.ConstraintViolations;
import com.example.protobuf.Detail;
import com.example.protobuf.ErrorResponse;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ErrorResponses {
    public static ErrorResponse toError(ConstraintViolations violations) {
        return ErrorResponse.newBuilder()
                .addAllDetails(violations.details().stream()
                        .map(d -> Detail.newBuilder()
                                .setKey(d.getKey())
                                .addAllArgs(Arrays.stream(d.getArgs()).map(Objects::toString).collect(Collectors.toList()))
                                .setDefaultMessage(d.getDefaultMessage())
                                .build())
                        .collect(Collectors.toList())).build();
    }
}
