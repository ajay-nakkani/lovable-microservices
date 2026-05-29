package com.ajay.lovable.commonlib.error;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> badRequestHandler(BadRequestException ex)
    {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());

        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> resourceNotFoundHandler(ResourceNotFoundException ex)
    {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getResourceId()+" name :"+ex.getResourceName());
        log.error(apiError.toString(),ex);
        return ResponseEntity.status(apiError.status()).body(apiError);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> noResourceFoundExceptionHandler(NoResourceFoundException ex)
    {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND,ex.getMessage());
        log.error(apiError.toString(),ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> InputValidationHandler(MethodArgumentNotValidException ex)
    {

        List<ApiFieldError> fieldErrors = ex.getBindingResult()
                                            .getFieldErrors()
                                            .stream()
                                            .map(err-> new ApiFieldError(err.getField(),err.getDefaultMessage()))
                                            .toList();
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,"Input Validation Error", fieldErrors );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

}
