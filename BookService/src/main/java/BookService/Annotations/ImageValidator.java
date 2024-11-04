package BookService.Annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.validation.annotation.Validated;

import BookService.Validator.ImageFormatValidator;
import BookService.Validator.PDFFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageFormatValidator.class)
public @interface ImageValidator {

	String message() default "File Must be of type JPG,JPEG & PNG type... ";
	Class<?>[] groups() default{};
	Class<? extends Payload>[] payload() default{};
}
