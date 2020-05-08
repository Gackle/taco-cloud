/**
 * Spring In Action 5th Bug Fixed
 * 自定义转换器将 String 转换为 Ingredient
 */
package sia.tacocloud.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import sia.tacocloud.dao.Ingredient;


@Component
public class IngredientByIdConverter implements Converter<String, Ingredient>{

    private IngredientRepository ingredientRepo;

    @Autowired
    public IngredientByIdConverter(IngredientRepository ingredientRepo) {
        this.ingredientRepo = ingredientRepo;
    }

    @Override
    public Ingredient convert(String id) {
        return ingredientRepo.findById(id);
    }
}
