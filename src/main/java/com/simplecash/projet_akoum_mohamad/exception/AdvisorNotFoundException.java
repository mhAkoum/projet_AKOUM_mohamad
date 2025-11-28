package com.simplecash.projet_akoum_mohamad.exception;

public class AdvisorNotFoundException extends BusinessException {
    
    public AdvisorNotFoundException(Long advisorId) {
        super(String.format("Advisor with ID %d not found", advisorId));
    }
}

