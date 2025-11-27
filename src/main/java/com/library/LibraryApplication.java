package com.library;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class LibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

	// --- Hada howa l-"Trigger" dyal demarrage ---
	// Kaykhdem automatic mra wehda mlli l-application kat-demarri
	@Bean
	CommandLineRunner checkSystemStatus(DataSource dataSource) {
		return args -> {
			System.out.println("\n==========================================================");
			System.out.println("   🚀 SYSTEM CHECK (VERIFICATION)");
			System.out.println("==========================================================");

			// 1. VERIFICATION DU PORT
			// Ila wsel l-code hna, ra l-port 8080 t7el mzyan, hit kon kan mochkil kon l-app crashat 9bel
			System.out.println("✅ PORT LOCALHOST : 8080 Mache3oule (Ouvert) - OK");

			// 2. VERIFICATION DATABASE ORACLE
			try (Connection conn = dataSource.getConnection()) {
				if (conn.isValid(2)) { // Test validité pendant 2 secondes
					System.out.println("✅ DATABASE ORACLE : Connecté (Mconnecti mzyan)");
					System.out.println("   ➜ URL : " + conn.getMetaData().getURL());
					System.out.println("   ➜ User: " + conn.getMetaData().getUserName());
				} else {
					System.out.println("❌ DATABASE ORACLE : Echec de connexion (Kayn mochkil)");
				}
			} catch (Exception e) {
				System.out.println("❌ DATABASE ORACLE : ERREUR GRAVE !");
				System.out.println("   🔴 Message: " + e.getMessage());
				System.out.println("   💡 Conseil: Verifier username/password f application.properties");
			}

			System.out.println("==========================================================\n");
		};
	}
}











// package com.library;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// public class LibraryApplication {

// 	public static void main(String[] args) {
// 		SpringApplication.run(LibraryApplication.class, args);
// 	}

// }