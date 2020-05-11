package sia.tacocloud.jpa;

import org.springframework.data.repository.CrudRepository;
import sia.tacocloud.dao.Taco;

/**
 * @ClassName TacoRepository
 * @Description extend CrudRepository interface for Taco
 * @Author Huang Jiahao
 * @Date 2020/5/11 11:24
 * @Version 1.0
 */
public interface TacoRepository extends CrudRepository<Taco, Long> {
}
