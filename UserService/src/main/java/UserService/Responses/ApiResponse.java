package UserService.Responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {

	private String message;
	private int status;
	private Object data;
}

