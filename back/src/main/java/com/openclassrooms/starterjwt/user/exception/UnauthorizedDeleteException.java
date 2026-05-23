package com.openclassrooms.starterjwt.user.exception;

public class UnauthorizedDeleteException extends RuntimeException {

    public UnauthorizedDeleteException() {
        super("You are not allowed to delete this account");
    }
}
