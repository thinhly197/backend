package com.ascend.campaign.validators;

import com.ascend.campaign.constants.FlashSaleEnum;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Constraint(validatedBy = FlashSaleConditionValidator.class)
@Documented
public @interface FlashSaleConditionCheck {
    String message() default "Value specified is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    FlashSaleEnum value();
}
