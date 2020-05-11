package sia.tacocloud.dao;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data // Data implicitly require a argument-constructor, but against with NoArgsConstructor
@RequiredArgsConstructor // ensure you have a argument constructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)  // JPA need a no-argument constructor
@Entity
@Table(name = "Ingredient")
public class Ingredient {
    @Id
    private final String id;
    private final String name;
    @Enumerated(EnumType.STRING)
    private final Type type;

    public static enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}

