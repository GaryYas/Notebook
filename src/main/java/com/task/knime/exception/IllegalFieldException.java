package com.task.knime.exception;

public class IllegalFieldException extends RuntimeException {

    private static final long serialVersionUID = -3860344765771841853L;

    public IllegalFieldException(String message) {
        super(message);
    }
}
