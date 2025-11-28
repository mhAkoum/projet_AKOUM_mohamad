package com.simplecash.projet_akoum_mohamad.dto;

import com.simplecash.projet_akoum_mohamad.domain.ClientType;

public class CreateClientRequest {
    
    private String name;
    private String address;
    private String phone;
    private String email;
    private ClientType clientType;
    private Long advisorId;
    
    public CreateClientRequest() {
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
}

