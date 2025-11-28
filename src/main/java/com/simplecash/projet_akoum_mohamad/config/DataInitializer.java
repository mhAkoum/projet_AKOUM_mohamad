package com.simplecash.projet_akoum_mohamad.config;

import com.simplecash.projet_akoum_mohamad.domain.*;
import com.simplecash.projet_akoum_mohamad.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;

@Configuration
public class DataInitializer {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Bean
    @Order(1)
    CommandLineRunner initDatabase(
            AgencyRepository agencyRepository,
            ManagerRepository managerRepository,
            AdvisorRepository advisorRepository,
            ClientRepository clientRepository) {
        
        return args -> {
            logger.info("Initializing test data...");
            
            Agency theAgency = new Agency("AG001", LocalDate.now());
            agencyRepository.save(theAgency);
            logger.info("Created agency: {}", theAgency.getCode());
            
            Manager kimo = new Manager("Kimo the Manager", "kimo@simplecash.com");
            kimo.setAgency(theAgency);
            theAgency.setManager(kimo);
            managerRepository.save(kimo);
            agencyRepository.save(theAgency);
            logger.info("Created manager: {} for agency: {}", kimo.getName(), theAgency.getCode());
            
            Advisor tchoupi = new Advisor("Tchoupi", "tchoupi@simplecash.com");
            tchoupi.setAgency(theAgency);
            theAgency.addAdvisor(tchoupi);
            advisorRepository.save(tchoupi);
            logger.info("Created advisor: {} for agency: {}", tchoupi.getName(), theAgency.getCode());
            
            Advisor dora = new Advisor("Dora the Explora", "dora@simplecash.com");
            dora.setAgency(theAgency);
            theAgency.addAdvisor(dora);
            advisorRepository.save(dora);
            logger.info("Created advisor: {} for agency: {}", dora.getName(), theAgency.getCode());
            
            Client john = new Client("John Sina", "Crater Base Alpha, Mars", "111-222-3333", "john.sina@mars.com", ClientType.PRIVATE);
            john.setAdvisor(tchoupi);
            tchoupi.addClient(john);
            clientRepository.save(john);
            logger.info("Created client: {} for advisor: {}", john.getName(), tchoupi.getName());
            
            Client epita = new Client("EPITA Students", "14-16 Rue Voltaire, Kremlin-Bicêtre", "01-44-08-00-00", "students@epita.fr", ClientType.BUSINESS);
            epita.setAdvisor(tchoupi);
            tchoupi.addClient(epita);
            clientRepository.save(epita);
            logger.info("Created client: {} for advisor: {}", epita.getName(), tchoupi.getName());
            
            Client marsGuy = new Client("Mars Explorer", "Red Planet Colony 5, Mars", "999-888-7777", "explorer@mars.com", ClientType.PRIVATE);
            marsGuy.setAdvisor(dora);
            dora.addClient(marsGuy);
            clientRepository.save(marsGuy);
            logger.info("Created client: {} for advisor: {}", marsGuy.getName(), dora.getName());
            
            logger.info("Test data initialization completed!");
            logger.info("Agency: {} with {} advisors and {} total clients", 
                    theAgency.getCode(), 
                    theAgency.getAdvisors().size(),
                    theAgency.getAdvisors().stream()
                            .mapToInt(advisor -> advisor.getClients().size())
                            .sum());
        };
    }
}

