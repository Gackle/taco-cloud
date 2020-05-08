package sia.tacocloud.data;

import sia.tacocloud.dao.Taco;

public interface TacoRepository {
    Taco save(Taco taco);
}
