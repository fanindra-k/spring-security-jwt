package com.kushwaha.book;

import com.kushwaha.book.role.Role;
import com.kushwaha.book.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class BookNetworkApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookNetworkApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			String[] roles = {"ADMIN", "USER", "INSTRUCTOR"};
			for(String role : roles) {
				if(roleRepository.findByName(role).isEmpty()) {
					roleRepository.save(Role.builder().name(role).build());
				}
			}
		};
	}
}
