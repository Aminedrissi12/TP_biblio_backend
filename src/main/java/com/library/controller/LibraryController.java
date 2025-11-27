package com.library.controller;

import com.library.model.*;
import com.library.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Import Value
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// --- REPOSITORIES INTERNES ---

@Repository
interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = "SELECT * FROM books WHERE stock < :limit", nativeQuery = true)
    List<Book> findBooksWithLowStock(@Param("limit") int limit);
    
    @Query("SELECT b FROM Book b WHERE b.category = :category")
    List<Book> findByCategory(@Param("category") String category);
}

@Repository
interface LoanRepository extends JpaRepository<Loan, Long> {
    @Query(value = "SELECT COUNT(*) FROM loans WHERE client_id = :clientId AND status = 'ACTIVE'", nativeQuery = true)
    int countActiveLoansByClient(@Param("clientId") Long clientId);
}

@Repository
interface ClientRepository extends JpaRepository<Client, Long> {}

// --- REST CONTROLLER PRINCIPAL ---

@RestController
@RequestMapping("/api")
// Hna sta3mlna SpEL (Spring Expression Language) bach njibou l-variable mn .env ola properties
@CrossOrigin(origins = "${FRONTEND_URL}") 
public class LibraryController {

    @Autowired private BookRepository bookRepo;
    @Autowired private LoanRepository loanRepo;
    @Autowired private ClientRepository clientRepo;
    
    @Autowired private AppUserRepository userRepo; 
    
    @Autowired private PasswordEncoder passwordEncoder;

    // Exemple: Kifach t-qra variable environement dakhle l-controller
    @Value("${SERVER_PORT}")
    private String serverPort;

    // ==========================================
    // 1. GESTION DES USERS (ADMIN)
    // ==========================================
    
    @GetMapping("/users")
    public List<AppUser> getAllUsers() {
        // Test: n-affichiw l-port f console bach n-t2akdo
        System.out.println("Running on port: " + serverPort);
        return userRepo.findAll();
    }

    @PostMapping("/users")
    public AppUser addUser(@RequestBody AppUser user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepo.save(user);
    }

    @PutMapping("/users/{id}")
    public AppUser updateUser(@PathVariable Long id, @RequestBody AppUser userDetails) {
        AppUser user = userRepo.findById(id).orElseThrow();
        user.setUsername(userDetails.getUsername());
        user.setEmail(userDetails.getEmail());
        user.setRole(userDetails.getRole());
        
        if(userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()){
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        return userRepo.save(user);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepo.deleteById(id);
    }
    
    // ==========================================
    // 2. AUTHENTIFICATION (LOGIN)
    // ==========================================
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String rawPassword = credentials.get("password");
        
        Map<String, Object> response = new HashMap<>();
        AppUser user = userRepo.findByEmail(email);
        
        if (user != null && passwordEncoder.matches(rawPassword, user.getPassword())) {
            response.put("success", true);
            response.put("user", user);
        } else {
            response.put("success", false);
            response.put("message", "Email ou mot de passe incorrect");
        }
        return response;
    }

    // ==========================================
    // 3. GESTION DES LIVRES (BOOKS)
    // ==========================================

    @GetMapping("/books")
    public List<Book> getAllBooks() { return bookRepo.findAll(); }

    @GetMapping("/books/low-stock")
    public List<Book> getLowStockBooks() { return bookRepo.findBooksWithLowStock(3); }

    @PostMapping("/books")
    public Book addBook(@RequestBody Book book) {
        if(book.getStock() == null) book.setStock(book.getTotalQty());
        return bookRepo.save(book);
    }

    // ==========================================
    // 4. GESTION DES CLIENTS
    // ==========================================

    @GetMapping("/clients")
    public List<Client> getAllClients() { return clientRepo.findAll(); }

    @PostMapping("/clients")
    public Client addClient(@RequestBody Client client) {
        client.setScore(100);
        client.setIsBlacklisted("N");
        return clientRepo.save(client);
    }

    // ==========================================
    // 5. GESTION DES EMPRUNTS (LOANS)
    // ==========================================

    @GetMapping("/loans")
    public List<Loan> getAllLoans() { return loanRepo.findAll(); }

    @PostMapping("/loans")
    public Map<String, Object> createLoan(@RequestBody Loan loan) {
        Map<String, Object> response = new HashMap<>();
        Long clientId = loan.getClient().getId();
        Client client = clientRepo.findById(clientId).orElseThrow();

        if ("Y".equals(client.getIsBlacklisted()) || client.getScore() <= 50) {
            response.put("error", true);
            response.put("message", "Client Blacklisté ou Score insuffisant!");
            return response;
        }

        if (loanRepo.countActiveLoansByClient(clientId) >= 3) {
            response.put("error", true);
            response.put("message", "Max 3 livres autorisés!");
            return response;
        }

        loan.setDateOut(LocalDate.now());
        loan.setStatus("ACTIVE");
        
        response.put("error", false);
        response.put("data", loanRepo.save(loan));
        return response;
    }

    @PutMapping("/loans/{id}/return")
    public Loan returnBook(@PathVariable Long id) {
        Loan loan = loanRepo.findById(id).orElseThrow();
        
        if("ACTIVE".equals(loan.getStatus())) {
            loan.setStatus("RETURNED");
            loan.setDateReturn(LocalDate.now());
            
            long daysLate = ChronoUnit.DAYS.between(loan.getDateOut(), LocalDate.now());
            if (daysLate > 10) {
                Client client = loan.getClient();
                int newScore = client.getScore() - 10;
                client.setScore(newScore);
                
                if (newScore <= 50) {
                    client.setIsBlacklisted("Y");
                }
                clientRepo.save(client);
            }
            return loanRepo.save(loan);
        }
        return loan;
    }

    // ==========================================
    // 6. STATISTIQUES (DASHBOARD)
    // ==========================================

    @GetMapping("/stats/books-out")
    public long countActiveLoans() {
        return loanRepo.findAll().stream().filter(l -> "ACTIVE".equals(l.getStatus())).count();
    }
}