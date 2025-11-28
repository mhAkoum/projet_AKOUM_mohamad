package com.simplecash.projet_akoum_mohamad.service;

import com.simplecash.projet_akoum_mohamad.domain.Advisor;
import com.simplecash.projet_akoum_mohamad.domain.Client;
import com.simplecash.projet_akoum_mohamad.domain.ClientType;
import com.simplecash.projet_akoum_mohamad.exception.AdvisorFullException;
import com.simplecash.projet_akoum_mohamad.exception.AdvisorNotFoundException;
import com.simplecash.projet_akoum_mohamad.exception.ClientNotFoundException;
import com.simplecash.projet_akoum_mohamad.repository.AdvisorRepository;
import com.simplecash.projet_akoum_mohamad.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientService {
    
    private static final int MAX_CLIENTS_PER_ADVISOR = 10;
    
    private final ClientRepository clientRepository;
    private final AdvisorRepository advisorRepository;
    
    @Autowired
    public ClientService(ClientRepository clientRepository, AdvisorRepository advisorRepository) {
        this.clientRepository = clientRepository;
        this.advisorRepository = advisorRepository;
    }
    
    public Client createClient(String name, String address, String phone, String email, 
                              ClientType clientType, Long advisorId) {
        Advisor advisor = advisorRepository.findById(advisorId)
                .orElseThrow(() -> new AdvisorNotFoundException(advisorId));
        
        if (advisor.getClients().size() >= MAX_CLIENTS_PER_ADVISOR) {
            throw new AdvisorFullException(advisorId);
        }
        
        Client client = new Client(name, address, phone, email, clientType);
        client.setAdvisor(advisor);
        advisor.addClient(client);
        
        return clientRepository.save(client);
    }
    
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
    
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }
    
    public Client updateClient(Long id, String name, String address, String phone, String email, ClientType clientType) {
        Client client = getClientById(id);
        
        if (name != null) {
            client.setName(name);
        }
        if (address != null) {
            client.setAddress(address);
        }
        if (phone != null) {
            client.setPhone(phone);
        }
        if (email != null) {
            client.setEmail(email);
        }
        if (clientType != null) {
            client.setClientType(clientType);
        }
        
        return clientRepository.save(client);
    }
    
    public void deleteClient(Long id) {
        Client client = getClientById(id);
        Advisor advisor = client.getAdvisor();
        
        if (advisor != null) {
            advisor.removeClient(client);
        }
        
        clientRepository.delete(client);
    }
    
    public List<Client> getClientsByAdvisorId(Long advisorId) {
        return clientRepository.findByAdvisorId(advisorId);
    }
}

