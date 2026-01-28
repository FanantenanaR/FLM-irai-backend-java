package com.flm.irai.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

// Classe generique de reponse API standardisee
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ApiResponse<T> {

    private ResponseStatus status;
    private int code;
    private String message;
    private T data;
    private String error;

    @JsonProperty("error_message")
    private String errorMessage;

    private Instant timestamp;

    // Champs de pagination
    private int page;
    private int limit;

    @JsonProperty("total_element")
    private long totalElement;

    @JsonProperty("number_page")
    private int numberPage;

    // Reponse de succes sans donnees
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .status(ResponseStatus.SUCCESS)
                .code(200)
                .message(message)
                .timestamp(Instant.now())
                .build();
    }

    // Reponse de succes avec donnees
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(ResponseStatus.SUCCESS)
                .code(200)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    // Reponse de succes avec code personnalise
    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return ApiResponse.<T>builder()
                .status(ResponseStatus.SUCCESS)
                .code(code)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    // Reponse de succes paginee
    public static <T> ApiResponse<T> successPaginated(String message, T data, int page, int limit, long totalElement) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Le parametre 'limit' doit etre superieur a 0");
        }
        int numberPage = (int) Math.ceil((double) totalElement / limit);
        return ApiResponse.<T>builder()
                .status(ResponseStatus.SUCCESS)
                .code(200)
                .message(message)
                .data(data)
                .page(page)
                .limit(limit)
                .totalElement(totalElement)
                .numberPage(numberPage)
                .timestamp(Instant.now())
                .build();
    }

    // Reponse d'erreur complete
    public static <T> ApiResponse<T> error(int code, String message, String error, String errorMessage) {
        return ApiResponse.<T>builder()
                .status(ResponseStatus.ERROR)
                .code(code)
                .message(message)
                .error(error)
                .errorMessage(errorMessage)
                .timestamp(Instant.now())
                .build();
    }

    // Reponse d'erreur simplifiee
    public static <T> ApiResponse<T> error(int code, String message, String error) {
        return ApiResponse.<T>builder()
                .status(ResponseStatus.ERROR)
                .code(code)
                .message(message)
                .error(error)
                .timestamp(Instant.now())
                .build();
    }

    // Erreur 400 - Bad Request
    public static <T> ApiResponse<T> badRequest(String message, String errorMessage) {
        return error(400, message, "BAD_REQUEST", errorMessage);
    }

    // Erreur 404 - Not Found
    public static <T> ApiResponse<T> notFound(String message) {
        return error(404, message, "NOT_FOUND");
    }

    // Erreur 409 - Conflict
    public static <T> ApiResponse<T> conflict(String message, String errorMessage) {
        return error(409, message, "CONFLICT", errorMessage);
    }

    // Erreur 500 - Internal Server Error
    public static <T> ApiResponse<T> internalError(String message, String errorMessage) {
        return error(500, message, "INTERNAL_ERROR", errorMessage);
    }
}
