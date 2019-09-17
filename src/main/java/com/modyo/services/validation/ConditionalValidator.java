package com.modyo.services.validation;

import static org.springframework.util.StringUtils.isEmpty;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

@Slf4j
public class ConditionalValidator implements ConstraintValidator<Conditional, Object> {

    private String selected;
    private String[] required;
    private String[] empty;
    private String message;
    private String[] values;

    @Override
    public void initialize(Conditional requiredIfChecked) {
        selected = requiredIfChecked.selected();
        required = requiredIfChecked.required();
        empty = requiredIfChecked.empty();
        message = requiredIfChecked.message();
        values = requiredIfChecked.values();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        Boolean valid = true;
        List<Boolean> valids = new ArrayList<>();
        try {
            Object checkedValue = BeanUtils.getProperty(object, selected);
            if (Arrays.asList(values).contains(checkedValue)) {
                System.out.println("checkedValue: " + checkedValue);
                for (String propName : required) {
                    Object requiredValue = BeanUtils.getProperty(object, propName);
                    valid = requiredValue != null && !isEmpty(requiredValue);
                    System.out.println(padSpaceRight(propName, 20) + " as requiredValue is: " + "" + valid);
                    if (!valid) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(message).addPropertyNode(propName).addConstraintViolation();
                    }
                    valids.add(valid);
                }
                for (String propName : empty) {
                    Object emptyValue = BeanUtils.getProperty(object, propName);
                    valid = emptyValue == null || isEmpty(emptyValue);
                    System.out.println(padSpaceRight(propName, 20) + " as emptyValue    is: " + "" + valid);
                    if (!valid) {
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(message).addPropertyNode(propName).addConstraintViolation();
                    }
                    valids.add(valid);
                }
            }
        } catch (IllegalAccessException e) {
            log.error("Accessor method is not available for class : {}, exception : {}", object.getClass().getName(), e);
            return false;
        } catch (NoSuchMethodException e) {
            log.error("Field or method is not present on class : {}, exception : {}", object.getClass().getName(), e);
            return false;
        } catch (InvocationTargetException e) {
            log.error("An exception occurred while accessing class : {}, exception : {}", object.getClass().getName(), e);
            return false;
        }
        for (Boolean aBoolean : valids) {
            if (!aBoolean){
                valid = false;
            }
        }
        return valid;
    }

    public String padSpaceRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
