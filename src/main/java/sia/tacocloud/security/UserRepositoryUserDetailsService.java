package sia.tacocloud.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sia.tacocloud.dao.User;
import sia.tacocloud.jpa.UserRepository;

/**
 * @ClassName UserRepositoryUserDetailsService
 * @Description TODO
 * @Author Huang Jiahao
 * @Date 2020/5/12 17:20
 * @Version 1.0
 */

@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {

    public UserRepository userRepo;

    @Autowired
    public UserRepositoryUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username);
        if (user != null) {
            return user;
        }
        throw new UsernameNotFoundException("User '" + username + "' not found");
    }
}
