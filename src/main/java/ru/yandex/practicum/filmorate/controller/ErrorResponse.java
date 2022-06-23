package ru.yandex.practicum.filmorate.controller;

public class ErrorResponse {
    private String error; // название ошибки
    private String description; // подробное описание

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
