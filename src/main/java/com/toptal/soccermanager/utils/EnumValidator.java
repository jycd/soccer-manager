package com.toptal.soccermanager.utils;

import javax.validation.*;
import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Documented
@Constraint(validatedBy = EnumValidator.EnumValidatorImpl.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ReportAsSingleViolation
public @interface EnumValidator {
    Class<? extends Enum<?>> enumClazz();

    String message() default "Value is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class EnumValidatorImpl implements ConstraintValidator<EnumValidator, String> {
        List<String> valueList = null;

        @Override
        public void initialize(EnumValidator constraintAnnotation) {
            valueList = new ArrayList<String>();
            Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClazz();

            @SuppressWarnings("rawtypes")
            Enum[] enumValArr = enumClass.getEnumConstants();

            for (@SuppressWarnings("rawtypes") Enum enumVal : enumValArr) {
                valueList.add(enumVal.toString().toUpperCase());
            }
        }

        @Override
        public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
            return s == null || valueList.contains(s.toUpperCase());
        }
    }
}
