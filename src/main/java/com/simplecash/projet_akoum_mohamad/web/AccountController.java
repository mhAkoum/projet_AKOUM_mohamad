package com.simplecash.projet_akoum_mohamad.web;

import com.simplecash.projet_akoum_mohamad.dto.AccountDTO;
import com.simplecash.projet_akoum_mohamad.dto.CreditRequest;
import com.simplecash.projet_akoum_mohamad.dto.DebitRequest;
import com.simplecash.projet_akoum_mohamad.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    
    private final AccountService accountService;
    
    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        AccountDTO account = new AccountDTO(accountService.getAccountById(id));
        return ResponseEntity.ok(account);
    }
    
    @PostMapping("/{id}/credit")
    public ResponseEntity<AccountDTO> creditAccount(
            @PathVariable Long id,
            @RequestBody CreditRequest request) {
        AccountDTO account = new AccountDTO(accountService.credit(id, request.getAmount()));
        return ResponseEntity.ok(account);
    }
    
    @PostMapping("/{id}/debit")
    public ResponseEntity<AccountDTO> debitAccount(
            @PathVariable Long id,
            @RequestBody DebitRequest request) {
        AccountDTO account = new AccountDTO(accountService.debit(id, request.getAmount()));
        return ResponseEntity.ok(account);
    }
}

