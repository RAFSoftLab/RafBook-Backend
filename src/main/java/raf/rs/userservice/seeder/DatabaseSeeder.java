package raf.rs.userservice.seeder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import raf.rs.userservice.model.Role;
import raf.rs.userservice.repository.RoleRepository;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {

        if (roleRepository.count() != 0) {
            return;
        }

        Role adminRole = new Role();
        adminRole.setName("ADMIN");

        roleRepository.save(adminRole);

    }
}
