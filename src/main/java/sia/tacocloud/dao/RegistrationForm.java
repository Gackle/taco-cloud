package sia.tacocloud.dao;

import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @ClassName RegistrationForm
 * @Description RegistrationForm ViewModel
 * @Author Huang Jiahao
 * @Date 2020/5/12 19:20
 * @Version 1.0
 */
@Data
public class RegistrationForm {
    private String username;
    private String password;
    private String fullname;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String phone;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(
                username, passwordEncoder.encode(password),
                fullname, street, city, state, zip, phone
        );
    }
}
