package com.library.repository;

import com.library.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    // Hada method bach n-l9aw user b l-email dyalo (login)
    AppUser findByEmail(String email);
}