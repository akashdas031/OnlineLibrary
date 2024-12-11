package UserService.Annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import UserService.Validators.ImageValidator;

import java.lang.annotation.ElementType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.RetentionPolicy;

@Documented
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageValidator.class)
public @interface ImageTypeValidator {

    String message() default "File Must be of type JPG,JPEG,JFIF & PNG type... ";
	Class<?>[] groups() default{};
	Class<? extends Payload>[] payload() default{};
}
