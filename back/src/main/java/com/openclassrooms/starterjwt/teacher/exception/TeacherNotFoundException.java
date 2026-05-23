package com.openclassrooms.starterjwt.teacher.exception;

public class TeacherNotFoundException extends RuntimeException {

    public TeacherNotFoundException(String identifier) {
        super("Teacher not found: " + identifier);
    }
}
