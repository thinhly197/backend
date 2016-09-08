package com.ascend.campaign.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class VerifyValueValidator implements ConstraintValidator<VerifyValue, Object> {

    Class<? extends Enum<?>> enumClass;

    public void initialize(final VerifyValue enumObject) {
        enumClass = enumObject.value();

    }

    public boolean isValid(final Object myval,
                           final ConstraintValidatorContext constraintValidatorContext) {


        if (myval != null && enumClass != null) {
            Enum[] enumValues = enumClass.getEnumConstants();
            Object enumValue = null;

            for (Enum enumerable : enumValues) {
                if (myval.equals(enumerable.toString())) {
                    return true;
                }
                enumValue = getEnumValue(enumerable);
                if (enumValue != null
                        && myval.toString().equals(enumValue.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Object getEnumValue(Enum<?> enumerable) {
        try {
            for (Method method : enumerable.getClass().getDeclaredMethods()) {
                if (method.getName().equals("getContent")) {
                    return method.invoke(enumerable);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


}
