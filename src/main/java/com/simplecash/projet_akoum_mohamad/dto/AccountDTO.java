package com.simplecash.projet_akoum_mohamad.dto;

import com.simplecash.projet_akoum_mohamad.domain.Account;
import com.simplecash.projet_akoum_mohamad.domain.CurrentAccount;
import com.simplecash.projet_akoum_mohamad.domain.SavingsAccount;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountDTO {
    
    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private LocalDate openingDate;
    private String accountType;
    private BigDecimal overdraftLimit;
    private BigDecimal interestRate;
    private Long clientId;
    private String clientName;
    
    public AccountDTO() {
    }
    
    public AccountDTO(Account account) {
        this.id = account.getId();
        this.accountNumber = account.getAccountNumber();
        this.balance = account.getBalance();
        this.openingDate = account.getOpeningDate();
        
        if (account instanceof CurrentAccount) {
            this.accountType = "CURRENT";
            CurrentAccount currentAccount = (CurrentAccount) account;
            this.overdraftLimit = currentAccount.getOverdraftLimit();
            if (currentAccount.getClient() != null) {
                this.clientId = currentAccount.getClient().getId();
                this.clientName = currentAccount.getClient().getName();
            }
        } else if (account instanceof SavingsAccount) {
            this.accountType = "SAVINGS";
            SavingsAccount savingsAccount = (SavingsAccount) account;
            this.interestRate = savingsAccount.getInterestRate();
            if (savingsAccount.getClient() != null) {
                this.clientId = savingsAccount.getClient().getId();
                this.clientName = savingsAccount.getClient().getName();
            }
        } else {
            this.accountType = "UNKNOWN";
        }
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    public LocalDate getOpeningDate() {
        return openingDate;
    }
    
    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }
    
    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
    
    public BigDecimal getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
    
    public Long getClientId() {
        return clientId;
    }
    
    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}

