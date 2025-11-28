package com.simplecash.projet_akoum_mohamad.exception;

public class ClientNotFoundException extends BusinessException {
    
    public ClientNotFoundException(Long clientId) {
        super(String.format("Client with ID %d not found", clientId));
    }
}

