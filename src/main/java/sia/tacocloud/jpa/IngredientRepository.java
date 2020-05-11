package sia.tacocloud.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import sia.tacocloud.dao.Ingredient;

import java.util.List;

/**
 * @ClassName IngredientRepository
 * @Description extend CrudRepository interface for Ingredient
 * @Author Huang Jiahao
 * @Date 2020/5/11 11:18
 * @Version 1.0
 */
public interface IngredientRepository
        extends CrudRepository<Ingredient, String>
        /* first parameter is entity type to be persist */
        /* second parameter is type of the entity ID */
{
    @Query("from Ingredient i where i.type='SAUCE'")
    List<Ingredient> readIngredientsInSAUCE();
}
