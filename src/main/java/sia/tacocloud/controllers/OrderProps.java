package sia.tacocloud.controllers;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @ClassName OrderProps
 * @Description Configuration properties holders
 * @Author Huang Jiahao
 * @Date 2020/5/15 16:53
 * @Version 1.0
 */

@Component
@ConfigurationProperties(prefix = "taco.orders")
@Data
@Validated
public class OrderProps {
    // 这个类的主要目的是解耦，将 ConfigurationProperties 的 holder 从 Controller 中解放出来，单独以 @Component 的 Bean 的形式进行依赖注入
    // 此后关于 Order Properties 的相关属性的增删都和 Controller 无关可以单独在这里设置然后给多个 Controller 所用
    // 设置可以单独针对某些 properties 进行校验
    @Min(value=5, message = "must be between 5 and 25")
    @Max(value = 25, message = "must be between 5 and 25")
    private int pageSize = 20;
}
