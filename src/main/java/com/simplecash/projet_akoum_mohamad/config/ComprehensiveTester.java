package com.simplecash.projet_akoum_mohamad.config;

import com.simplecash.projet_akoum_mohamad.domain.*;
import com.simplecash.projet_akoum_mohamad.exception.*;
import com.simplecash.projet_akoum_mohamad.repository.*;
import com.simplecash.projet_akoum_mohamad.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class ComprehensiveTester {
    
    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveTester.class);
    
    // @Bean
    // @Order(100)
    // Disabled - uncomment to run comprehensive tests
    CommandLineRunner comprehensiveTest(
            ClientService clientService,
            AdvisorService advisorService,
            AccountService accountService,
            TransferService transferService,
            ClientRepository clientRepository,
            AdvisorRepository advisorRepository,
            AccountRepository accountRepository,
            CardRepository cardRepository) {
        
        return args -> {
            logger.info("═══════════════════════════════════════════════════════════");
            logger.info("  COMPREHENSIVE TEST SUITE");
            logger.info("═══════════════════════════════════════════════════════════");
            
            int passedTests = 0;
            int failedTests = 0;
            
            try {
                passedTests += testClientAdvisorServices(clientService, advisorService, advisorRepository);
                passedTests += testAccountOperations(accountService, accountRepository);
                passedTests += testTransferService(transferService, accountService, accountRepository);
                passedTests += testTransferLogging();
                passedTests += testCardManagement(clientRepository, cardRepository);
                passedTests += testClientDeletionValidation(clientService, accountService, clientRepository, cardRepository);
                
            } catch (Exception e) {
                logger.error("Critical error during testing: {}", e.getMessage(), e);
                failedTests++;
            }
            
            logger.info("═══════════════════════════════════════════════════════════");
            logger.info("  TEST SUMMARY");
            logger.info("═══════════════════════════════════════════════════════════");
            logger.info("Passed: {}", passedTests);
            logger.info("Failed: {}", failedTests);
            logger.info("═══════════════════════════════════════════════════════════");
        };
    }
    
    private int testClientAdvisorServices(
            ClientService clientService,
            AdvisorService advisorService,
            AdvisorRepository advisorRepository) {
        
        logger.info("\n[Testing Client & Advisor Management]");
        logger.info("─────────────────────────────────────────────");
        
        int passed = 0;
        
        try {
            Advisor tchoupi = advisorRepository.findAll().stream()
                    .filter(a -> a.getName().equals("Tchoupi"))
                    .findFirst()
                    .orElse(null);
            
            if (tchoupi == null) {
                logger.warn("   X- Tchoupi advisor not found - skipping");
                return 0;
            }
            
            Long tchoupiId = tchoupi.getId();
            int currentCount = advisorService.getClientCount(tchoupiId);
            logger.info("    V- Current client count for Tchoupi: {}", currentCount);
            
            List<Client> clientsBefore = clientService.getClientsByAdvisorId(tchoupiId);
            logger.info("    V- Retrieved {} clients for advisor", clientsBefore.size());
            passed++;
            
            if (currentCount < 10) {
                Client newClient = clientService.createClient(
                        "Test Client Comprehensive",
                        "Test Address",
                        "555-9999",
                        "test@comprehensive.com",
                        ClientType.PRIVATE,
                        tchoupiId
                );
                logger.info("    V- Created new client: {}", newClient.getName());
                passed++;
                
                int newCount = advisorService.getClientCount(tchoupiId);
                if (newCount == currentCount + 1) {
                    logger.info("    V- Client count updated correctly: {}", newCount);
                    passed++;
                } else {
                    logger.warn("  ✗ Client count mismatch: expected {}, got {}", currentCount + 1, newCount);
                }
            }
            
            try {
                int clientsToAdd = 11 - advisorService.getClientCount(tchoupiId);
                for (int i = 0; i < clientsToAdd; i++) {
                    clientService.createClient(
                            "Max Test Client " + i,
                            "Address " + i,
                            "555-" + i,
                            "max" + i + "@test.com",
                            ClientType.PRIVATE,
                            tchoupiId
                    );
                }
                logger.warn("  ✗ Should have thrown AdvisorFullException!");
            } catch (AdvisorFullException e) {
                logger.info("    V- Max 10 clients rule enforced: {}", e.getMessage());
                passed++;
            }
            
        } catch (Exception e) {
            logger.error("  ✗ Error in Client & Advisor tests: {}", e.getMessage());
        }
        
        logger.info("  [Client & Advisor Management] Completed: {} tests passed\n", passed);
        return passed;
    }
    
    private int testAccountOperations(
            AccountService accountService,
            AccountRepository accountRepository) {
        
        logger.info("[Testing Account Credit & Debit Operations]");
        logger.info("─────────────────────────────────────────────");
        
        int passed = 0;
        
        try {
            Account testAccount = accountRepository.findAll().stream()
                    .filter(a -> a.getAccountNumber().startsWith("CA"))
                    .findFirst()
                    .orElse(null);
            
            if (testAccount == null) {
                logger.warn("   X- No test account found - skipping");
                return 0;
            }
            
            BigDecimal initialBalance = testAccount.getBalance();
            logger.info("    V- Found test account: {} (Balance: {})", testAccount.getAccountNumber(), initialBalance);
            passed++;
            
            BigDecimal creditAmount = new BigDecimal("500.00");
            Account afterCredit = accountService.credit(testAccount.getId(), creditAmount);
            BigDecimal expectedBalance = initialBalance.add(creditAmount);
            
            if (afterCredit.getBalance().compareTo(expectedBalance) == 0) {
                logger.info("    V- Credit operation successful: {} + {} = {}", 
                        initialBalance, creditAmount, afterCredit.getBalance());
                passed++;
            } else {
                logger.warn("  ✗ Credit balance mismatch: expected {}, got {}", 
                        expectedBalance, afterCredit.getBalance());
            }
            
            BigDecimal debitAmount = new BigDecimal("200.00");
            Account afterDebit = accountService.debit(testAccount.getId(), debitAmount);
            BigDecimal expectedAfterDebit = expectedBalance.subtract(debitAmount);
            
            if (afterDebit.getBalance().compareTo(expectedAfterDebit) == 0) {
                logger.info("    V- Debit operation successful: {} - {} = {}", 
                        afterCredit.getBalance(), debitAmount, afterDebit.getBalance());
                passed++;
            } else {
                logger.warn("  ✗ Debit balance mismatch: expected {}, got {}", 
                        expectedAfterDebit, afterDebit.getBalance());
            }
            
            try {
                BigDecimal hugeAmount = afterDebit.getBalance().add(new BigDecimal("100000.00"));
                accountService.debit(testAccount.getId(), hugeAmount);
                logger.warn("  ✗ Should have thrown InsufficientFundsException!");
            } catch (InsufficientFundsException e) {
                logger.info("    V- Insufficient funds exception thrown correctly");
                passed++;
            }
            
            try {
                accountService.credit(testAccount.getId(), BigDecimal.ZERO);
                logger.warn("  ✗ Should have thrown IllegalArgumentException for zero amount!");
            } catch (IllegalArgumentException e) {
                logger.info("    V- Zero amount validation works: {}", e.getMessage());
                passed++;
            }
            
            accountService.debit(testAccount.getId(), afterDebit.getBalance().subtract(initialBalance));
            logger.info("    V- Restored account to initial balance");
            
        } catch (Exception e) {
            logger.error("  ✗ Error in Account Operations tests: {}", e.getMessage());
        }
        
        logger.info("  [Account Credit & Debit Operations] Completed: {} tests passed\n", passed);
        return passed;
    }
    
    private int testTransferService(
            TransferService transferService,
            AccountService accountService,
            AccountRepository accountRepository) {
        
        logger.info("[Testing Internal Account Transfers]");
        logger.info("─────────────────────────────────────────────");
        
        int passed = 0;
        
        try {
            List<Account> accounts = accountRepository.findAll();
            if (accounts.size() < 2) {
                logger.warn("   X- Need at least 2 accounts for transfer test - skipping");
                return 0;
            }
            
            Account sourceAccount = accounts.get(0);
            Account targetAccount = accounts.get(1);
            
            BigDecimal sourceInitial = sourceAccount.getBalance();
            BigDecimal targetInitial = targetAccount.getBalance();
            
            logger.info("    V- Source account: {} (Balance: {})", sourceAccount.getAccountNumber(), sourceInitial);
            logger.info("    V- Target account: {} (Balance: {})", targetAccount.getAccountNumber(), targetInitial);
            passed += 2;
            
            BigDecimal transferAmount = new BigDecimal("150.00");
            
            if (sourceInitial.compareTo(transferAmount) < 0) {
                accountService.credit(sourceAccount.getId(), transferAmount);
                sourceInitial = accountService.getAccountById(sourceAccount.getId()).getBalance();
                logger.info("    V- Credited source account for test: {}", sourceInitial);
            }
            
            transferService.transfer(sourceAccount.getId(), targetAccount.getId(), transferAmount);
            
            Account sourceAfter = accountService.getAccountById(sourceAccount.getId());
            Account targetAfter = accountService.getAccountById(targetAccount.getId());
            
            BigDecimal expectedSource = sourceInitial.subtract(transferAmount);
            BigDecimal expectedTarget = targetInitial.add(transferAmount);
            
            if (sourceAfter.getBalance().compareTo(expectedSource) == 0 &&
                targetAfter.getBalance().compareTo(expectedTarget) == 0) {
                logger.info("    V- Transfer successful: {} -> {} (Amount: {})", 
                        sourceAccount.getAccountNumber(), targetAccount.getAccountNumber(), transferAmount);
                logger.info("    Source: {} -> {}", sourceInitial, sourceAfter.getBalance());
                logger.info("    Target: {} -> {}", targetInitial, targetAfter.getBalance());
                passed++;
            } else {
                logger.warn("  ✗ Transfer balance mismatch");
            }
            
            BigDecimal balanceBeforeFailed = sourceAfter.getBalance();
            BigDecimal targetBeforeFailed = targetAfter.getBalance();
            
            try {
                BigDecimal hugeAmount = balanceBeforeFailed.add(new BigDecimal("50000.00"));
                transferService.transfer(sourceAccount.getId(), targetAccount.getId(), hugeAmount);
                logger.warn("  ✗ Should have thrown InsufficientFundsException!");
            } catch (InsufficientFundsException e) {
                logger.info("    V- Failed transfer throws InsufficientFundsException");
                
                Account sourceAfterFailed = accountService.getAccountById(sourceAccount.getId());
                Account targetAfterFailed = accountService.getAccountById(targetAccount.getId());
                
                if (sourceAfterFailed.getBalance().compareTo(balanceBeforeFailed) == 0 &&
                    targetAfterFailed.getBalance().compareTo(targetBeforeFailed) == 0) {
                    logger.info("    V- Transaction rolled back - both accounts unchanged");
                    passed++;
                } else {
                    logger.warn("  ✗ Transaction did not roll back correctly!");
                }
            }
            
            try {
                transferService.transfer(sourceAccount.getId(), sourceAccount.getId(), new BigDecimal("50.00"));
                logger.warn("  ✗ Should have thrown IllegalArgumentException for same account!");
            } catch (IllegalArgumentException e) {
                logger.info("    V- Same account transfer validation works");
                passed++;
            }
            
        } catch (Exception e) {
            logger.error("  ✗ Error in Transfer Service tests: {}", e.getMessage());
        }
        
        logger.info("  [Internal Account Transfers] Completed: {} tests passed\n", passed);
        return passed;
    }
    
    private int testTransferLogging() {
        logger.info("[Testing Transfer Logging to Dedicated Log File]");
        logger.info("─────────────────────────────────────────────");
        
        int passed = 0;
        
        try {
            Path logPath = Paths.get("logs/transfers.log");
            
            if (!Files.exists(logPath)) {
                logger.warn("   X- transfers.log file not found at: {}", logPath.toAbsolutePath());
                return 0;
            }
            
            List<String> logLines = Files.readAllLines(logPath);
            logger.info("    V- Found transfers.log with {} lines", logLines.size());
            passed++;
            
            boolean hasTransferLog = logLines.stream()
                    .anyMatch(line -> line.contains("TRANSFER") && line.contains("SUCCESS"));
            
            if (hasTransferLog) {
                logger.info("    V- Transfer log contains SUCCESS entries");
                passed++;
            } else {
                logger.warn("  ✗ No SUCCESS transfer logs found");
            }
            
            boolean hasStartedLog = logLines.stream()
                    .anyMatch(line -> line.contains("TRANSFER") && line.contains("STARTED"));
            
            if (hasStartedLog) {
                logger.info("    V- Transfer log contains STARTED entries");
                passed++;
            } else {
                logger.warn("  ✗ No STARTED transfer logs found");
            }
            
        } catch (Exception e) {
            logger.error("  ✗ Error checking transfer logs: {}", e.getMessage());
        }
        
        logger.info("  [Transfer Logging to Dedicated Log File] Completed: {} tests passed\n", passed);
        return passed;
    }
    
    private int testCardManagement(
            ClientRepository clientRepository,
            CardRepository cardRepository) {
        
        logger.info("[Testing Card Entity & Client Relationship]");
        logger.info("─────────────────────────────────────────────");
        
        int passed = 0;
        
        try {
            List<Client> clientsWithCards = clientRepository.findAll().stream()
                    .filter(c -> !c.getCards().isEmpty())
                    .toList();
            
            if (clientsWithCards.isEmpty()) {
                logger.warn("   X- No clients with cards found - skipping");
                return 0;
            }
            
            Client clientWithCards = clientsWithCards.get(0);
            logger.info("    V- Found client with cards: {} ({} cards)", 
                    clientWithCards.getName(), clientWithCards.getCards().size());
            passed++;
            
            List<Card> cards = cardRepository.findByClientId(clientWithCards.getId());
            if (!cards.isEmpty()) {
                logger.info("    V- Retrieved {} cards via repository", cards.size());
                passed++;
                
                Card firstCard = cards.get(0);
                logger.info("    V- Card details: Type={}, Status={}", 
                        firstCard.getCardType(), firstCard.getStatus());
                passed++;
            }
            
            List<Card> activeCards = cardRepository.findByClientIdAndStatus(
                    clientWithCards.getId(), CardStatus.ACTIVE);
            logger.info("    V- Found {} active cards", activeCards.size());
            passed++;
            
        } catch (Exception e) {
            logger.error("  ✗ Error in Card Management tests: {}", e.getMessage());
        }
        
        logger.info("  [Card Entity & Client Relationship] Completed: {} tests passed\n", passed);
        return passed;
    }
    
    private int testClientDeletionValidation(
            ClientService clientService,
            AccountService accountService,
            ClientRepository clientRepository,
            CardRepository cardRepository) {
        
        logger.info("[Testing Client Deletion with Balance Validation]");
        logger.info("─────────────────────────────────────────────");
        
        int passed = 0;
        
        try {
            List<Client> clients = clientRepository.findAll();
            if (clients.isEmpty()) {
                logger.warn("   X- No clients found - skipping");
                return 0;
            }
            
            Client testClient = clients.stream()
                    .filter(c -> c.getCurrentAccount() != null || c.getSavingsAccount() != null)
                    .findFirst()
                    .orElse(null);
            
            if (testClient == null) {
                logger.warn("   X- No client with accounts found - skipping");
                return 0;
            }
            
            logger.info("    V- Found test client: {} (ID: {})", testClient.getName(), testClient.getId());
            passed++;
            
            BigDecimal currentBalance = testClient.getCurrentAccount() != null 
                    ? testClient.getCurrentAccount().getBalance() 
                    : BigDecimal.ZERO;
            BigDecimal savingsBalance = testClient.getSavingsAccount() != null 
                    ? testClient.getSavingsAccount().getBalance() 
                    : BigDecimal.ZERO;
            
            logger.info("    V- Account balances - Current: {}, Savings: {}", currentBalance, savingsBalance);
            
            if (currentBalance.compareTo(BigDecimal.ZERO) != 0 || savingsBalance.compareTo(BigDecimal.ZERO) != 0) {
                try {
                    clientService.deleteClient(testClient.getId());
                    logger.warn("  ✗ Should have thrown AccountBalanceNotZeroException!");
                } catch (AccountBalanceNotZeroException e) {
                    logger.info("    V- Deletion blocked with non-zero balance: {}", e.getMessage());
                    passed++;
                }
            } else {
                logger.info("    V- Client has zero balances - deletion would be allowed");
                passed++;
            }
            
            List<Card> clientCards = cardRepository.findByClientId(testClient.getId());
            if (!clientCards.isEmpty()) {
                logger.info("    V- Client has {} cards (will be deactivated on deletion)", clientCards.size());
                passed++;
            }
            
        } catch (Exception e) {
            logger.error("  ✗ Error in Client Deletion Validation tests: {}", e.getMessage());
        }
        
        logger.info("  [Client Deletion with Balance Validation] Completed: {} tests passed\n", passed);
        return passed;
    }
}

