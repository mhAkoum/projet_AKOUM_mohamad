package com.simplecash.projet_akoum_mohamad.exception;

public class AccountBalanceNotZeroException extends BusinessException {
    
    public AccountBalanceNotZeroException(Long clientId, String accountType, String accountNumber) {
        super(String.format("Cannot delete client %d: %s account %s has non-zero balance. Balance must be 0 to delete the client.", 
            clientId, accountType, accountNumber));
    }
}

