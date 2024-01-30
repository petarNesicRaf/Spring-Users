package rs.raf.springusers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import rs.raf.springusers.entities.Role;
import rs.raf.springusers.entities.User;
import rs.raf.springusers.repository.UserRepository;

@SpringBootApplication
public class SpringUsersApplication implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    public void run(String... args)
    {
        User adminAccount = userRepository.findByRole(Role.ADMIN);
        if(null == adminAccount)
        {
            User user = new User();
            user.setEmail("admin@gmail.com");
            user.setFirstName("admin");
            user.setSecondName("admin");
            user.setRole(Role.ADMIN);
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            userRepository.save(user);
        }
    }
    public static void main(String[] args) {
        SpringApplication.run(SpringUsersApplication.class, args);
    }

}
