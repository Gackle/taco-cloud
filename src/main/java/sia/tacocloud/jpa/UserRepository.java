package sia.tacocloud.jpa;


import org.springframework.data.repository.CrudRepository;
import sia.tacocloud.dao.User;

/**
 * @ClassName UserRepository
 * @Description JPA User Repository
 * @Author Huang Jiahao
 * @Date 2020/5/12 16:55
 * @Version 1.0
 */
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
