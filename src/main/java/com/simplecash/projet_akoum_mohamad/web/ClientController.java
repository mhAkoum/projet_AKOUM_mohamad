package com.simplecash.projet_akoum_mohamad.web;

import com.simplecash.projet_akoum_mohamad.dto.ClientDTO;
import com.simplecash.projet_akoum_mohamad.dto.CreateClientRequest;
import com.simplecash.projet_akoum_mohamad.dto.UpdateClientRequest;
import com.simplecash.projet_akoum_mohamad.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clients")
public class ClientController {
    
    private final ClientService clientService;
    
    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }
    
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        List<ClientDTO> clients = clientService.getAllClients().stream()
                .map(ClientDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clients);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        ClientDTO client = new ClientDTO(clientService.getClientById(id));
        return ResponseEntity.ok(client);
    }
    
    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@RequestBody CreateClientRequest request) {
        ClientDTO client = new ClientDTO(clientService.createClient(
                request.getName(),
                request.getAddress(),
                request.getPhone(),
                request.getEmail(),
                request.getClientType(),
                request.getAdvisorId()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(client);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(
            @PathVariable Long id,
            @RequestBody UpdateClientRequest request) {
        ClientDTO client = new ClientDTO(clientService.updateClient(
                id,
                request.getName(),
                request.getAddress(),
                request.getPhone(),
                request.getEmail(),
                request.getClientType()
        ));
        return ResponseEntity.ok(client);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}

