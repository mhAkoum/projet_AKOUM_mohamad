package com.simplecash.projet_akoum_mohamad.exception;

public class AdvisorFullException extends BusinessException {
    
    private static final int MAX_CLIENTS = 10;
    
    public AdvisorFullException(Long advisorId) {
        super(String.format("Advisor with ID %d already has the maximum of %d clients", advisorId, MAX_CLIENTS));
    }
    
    public AdvisorFullException(String advisorName) {
        super(String.format("Advisor '%s' already has the maximum of %d clients", advisorName, MAX_CLIENTS));
    }
}

