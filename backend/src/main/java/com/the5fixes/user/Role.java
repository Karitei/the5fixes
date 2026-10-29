package com.the5fixes.user;

/**
 * Kept deliberately small. Add roles only when a real permission boundary exists —
 * role proliferation is how authorisation bugs are born.
 */
public enum Role {
    USER,
    MANAGER,
    ADMIN
}
