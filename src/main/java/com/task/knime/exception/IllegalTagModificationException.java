package com.task.knime.exception;

public class IllegalTagModificationException extends RuntimeException {

    private static final long serialVersionUID = -3860344765771841853L;

    public IllegalTagModificationException(String message) {
        super(message);
    }
}
