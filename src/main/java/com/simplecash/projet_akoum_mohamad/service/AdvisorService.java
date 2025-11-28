package com.simplecash.projet_akoum_mohamad.service;

import com.simplecash.projet_akoum_mohamad.domain.Advisor;
import com.simplecash.projet_akoum_mohamad.domain.Client;
import com.simplecash.projet_akoum_mohamad.exception.AdvisorNotFoundException;
import com.simplecash.projet_akoum_mohamad.repository.AdvisorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdvisorService {
    
    private final AdvisorRepository advisorRepository;
    
    @Autowired
    public AdvisorService(AdvisorRepository advisorRepository) {
        this.advisorRepository = advisorRepository;
    }
    
    public List<Advisor> getAllAdvisors() {
        return advisorRepository.findAll();
    }
    
    public Advisor getAdvisorById(Long id) {
        return advisorRepository.findById(id)
                .orElseThrow(() -> new AdvisorNotFoundException(id));
    }
    
    public List<Client> getClientsByAdvisorId(Long advisorId) {
        Advisor advisor = getAdvisorById(advisorId);
        return advisor.getClients();
    }
    
    public List<Advisor> getAdvisorsByAgencyId(Long agencyId) {
        return advisorRepository.findByAgencyId(agencyId);
    }
    
    public int getClientCount(Long advisorId) {
        Advisor advisor = getAdvisorById(advisorId);
        return advisor.getClients().size();
    }
}

