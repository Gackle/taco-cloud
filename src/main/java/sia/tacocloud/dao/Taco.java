package sia.tacocloud.dao;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

@Data
public class Taco {
    private Long id;

    private Date createdAt;

    @NotNull
    @Size(min=5, message = "Name must be at least 5 character long")
    private String name;

    @NotNull
    @Size(min=1, message = "You must choose at least 1 ingredient")
    private List<String> ingredients;
}
