package com.simplecash.projet_akoum_mohamad.config;

import com.simplecash.projet_akoum_mohamad.domain.ClientType;
import com.simplecash.projet_akoum_mohamad.exception.AdvisorFullException;
import com.simplecash.projet_akoum_mohamad.repository.AdvisorRepository;
import com.simplecash.projet_akoum_mohamad.service.AdvisorService;
import com.simplecash.projet_akoum_mohamad.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ServiceTester {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceTester.class);
    
    @Bean
    @Order(2)
    CommandLineRunner testServices(
            ClientService clientService,
            AdvisorService advisorService,
            AdvisorRepository advisorRepository) {
        
        return args -> {
            logger.info("=== Testing Services ===");
            
            Long tchoupiId = advisorRepository.findAll().stream()
                    .filter(a -> a.getName().equals("Tchoupi"))
                    .findFirst()
                    .map(a -> a.getId())
                    .orElse(null);
            
            if (tchoupiId == null) {
                logger.warn("Tchoupi advisor not found, skipping service tests");
                return;
            }
            
            logger.info("Testing max 10 clients per advisor rule..");
            logger.info("Current client count for Tchoupi: {}", advisorService.getClientCount(tchoupiId));
            
            int currentCount = advisorService.getClientCount(tchoupiId);
            int clientsToAdd = 11 - currentCount;
            
            logger.info("Adding {} clients to test the limit...", clientsToAdd);
            
            for (int i = 1; i <= clientsToAdd; i++) {
                try {
                    clientService.createClient(
                            "Test Client " + i,
                            "Test Address " + i,
                            "555-000" + i,
                            "test" + i + "@test.com",
                            ClientType.PRIVATE,
                            tchoupiId
                    );
                    logger.info(" Created client {} of {}", i, clientsToAdd);
                } catch (AdvisorFullException e) {
                    logger.info(" Max clients rule enforced! Exception: {}", e.getMessage());
                    break;
                }
            }
            
            logger.info("Final client count for Tchoupi: {}", advisorService.getClientCount(tchoupiId));
            logger.info("=== Service Tests Completed ===");
        };
    }
}

