package sia.tacocloud.data;

import sia.tacocloud.dao.Order;

public interface OrderRepository {
    Order save(Order order);
}
