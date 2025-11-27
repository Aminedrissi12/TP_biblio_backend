package com.library.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private LocalDate dateOut;
    private LocalDate dateReturn;
    private String status; // 'ACTIVE', 'RETURNED'

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }
    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
    public LocalDate getDateOut() { return dateOut; }
    public void setDateOut(LocalDate dateOut) { this.dateOut = dateOut; }
    public LocalDate getDateReturn() { return dateReturn; }
    public void setDateReturn(LocalDate dateReturn) { this.dateReturn = dateReturn; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}