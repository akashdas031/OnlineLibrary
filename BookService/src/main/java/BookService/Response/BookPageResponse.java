package BookService.Response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
public class BookPageResponse {

	private int currentPage;
	private int totalPage;
	private String content;
	private boolean hasNext;
	private boolean hasPrevious;
	
	public BookPageResponse(int currentPage,int totalPage,String content) {
		this.currentPage=currentPage;
		this.totalPage=totalPage;
		this.content=content;
		this.hasNext=hasNext;
		this.hasPrevious=hasPrevious;
	}
}
