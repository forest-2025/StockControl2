package com.example.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class WebExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(NotFoundException e)  {
		log.info("404 NotFound occurred: {}", e.getMessage());
		log.error("スタックトレース", e);
        return new ModelAndView("404");
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage());
        log.error("スタックトレース", e);
        return new ModelAndView("error");
    }

}
