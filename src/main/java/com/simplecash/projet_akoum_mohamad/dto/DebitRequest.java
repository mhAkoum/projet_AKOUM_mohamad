package com.simplecash.projet_akoum_mohamad.dto;

import java.math.BigDecimal;

public class DebitRequest {
    
    private BigDecimal amount;
    private String description;
    
    public DebitRequest() {
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}

