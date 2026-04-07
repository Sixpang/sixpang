package org.sixpang.hubservice.application.dto;

import java.util.List;

public record DirectionsResponseDto(int code, String message, Route route) {
    public record Route(List<Result> traoptimal) {}
    public record Result(Summary summary) {}
    public record Summary(int distance, int duration) {}
}
