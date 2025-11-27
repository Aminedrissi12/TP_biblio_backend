package com.library.model;

import jakarta.persistence.*;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String cin;
    private String phone;
    
    private Integer score = 100; // Default score
    @Column(name = "is_blacklisted")
    private String isBlacklisted = "N"; // 'Y' or 'N'

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getCin() { return cin; }
    public void setCin(String cin) { this.cin = cin; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    public String getIsBlacklisted() { return isBlacklisted; }
    public void setIsBlacklisted(String isBlacklisted) { this.isBlacklisted = isBlacklisted; }
}