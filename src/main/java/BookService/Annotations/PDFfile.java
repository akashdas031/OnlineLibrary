package BookService.Annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import BookService.Validator.PDFFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = PDFFileValidator.class)
@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PDFfile {

	String message() default "File Must be a PDF ";
	Class<?>[] groups() default{};
	Class<? extends Payload>[] payload() default{};
}
