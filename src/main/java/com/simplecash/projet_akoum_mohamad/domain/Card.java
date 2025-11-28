package com.simplecash.projet_akoum_mohamad.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "cards")
public class Card {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false)
    private CardType cardType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CardStatus status;
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
    
    public Card() {
    }
    
    public Card(CardType cardType, CardStatus status) {
        this.cardType = cardType;
        this.status = status;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public CardType getCardType() {
        return cardType;
    }
    
    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }
    
    public CardStatus getStatus() {
        return status;
    }
    
    public void setStatus(CardStatus status) {
        this.status = status;
    }
    
    public Client getClient() {
        return client;
    }
    
    public void setClient(Client client) {
        this.client = client;
    }
}

