package com.simplecash.projet_akoum_mohamad.dto;

import com.simplecash.projet_akoum_mohamad.domain.Client;
import com.simplecash.projet_akoum_mohamad.domain.ClientType;

public class ClientDTO {
    
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private ClientType clientType;
    private Long advisorId;
    private String advisorName;
    
    public ClientDTO() {
    }
    
    public ClientDTO(Client client) {
        this.id = client.getId();
        this.name = client.getName();
        this.address = client.getAddress();
        this.phone = client.getPhone();
        this.email = client.getEmail();
        this.clientType = client.getClientType();
        if (client.getAdvisor() != null) {
            this.advisorId = client.getAdvisor().getId();
            this.advisorName = client.getAdvisor().getName();
        }
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public ClientType getClientType() {
        return clientType;
    }
    
    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }
    
    public Long getAdvisorId() {
        return advisorId;
    }
    
    public void setAdvisorId(Long advisorId) {
        this.advisorId = advisorId;
    }
    
    public String getAdvisorName() {
        return advisorName;
    }
    
    public void setAdvisorName(String advisorName) {
        this.advisorName = advisorName;
    }
}

