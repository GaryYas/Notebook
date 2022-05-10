package com.task.knime.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ExceptionDetails {

    private Date date;
    private String details;
    private String message;
}
