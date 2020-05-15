package sia.tacocloud.jpa;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import sia.tacocloud.dao.Order;
import sia.tacocloud.dao.User;

import java.util.Date;
import java.util.List;

/**
 * @ClassName OrderRepository
 * @Description extend CrudRepository interface for Order
 * @Author Huang Jiahao
 * @Date 2020/5/11 11:25
 * @Version 1.0
 */
public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> findByZip(String Zip);

    int countOrdersByZipAndPlacedAtBetween(String Zip, Date startDate, Date endDate);

    List<Order> findByUserOrderByPlacedAtDesc(User user, Pageable pageable);

//    @Query("Order o where o.city='Seattle'")
//    List<Order> readOrdersDeliveredInSeattle();
}
