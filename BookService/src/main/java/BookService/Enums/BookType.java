package BookService.Enums;

import java.util.HashSet;
import java.util.Set;

public enum BookType {

	EBOOK,AUDIO_BOOK,HARD_COVER,PAPER_BACK,MAGAZINE;
	private static final Set<String> VALID_BOOKTYPES=new HashSet<>();
	static {
		for(BookType book:BookType.values()) {
			VALID_BOOKTYPES.add(book.name());
		}
	}
	
	//check whether a string is a valid Enum type or not
	public static boolean isValidBookType(String bookType) {
		return bookType !=null && VALID_BOOKTYPES.contains(bookType);
	}
}
