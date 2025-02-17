package BookReadingProgressService.Services;

import BookReadingProgressService.Entities.ReadingProgress;

public interface ReadingProgressService {

	ReadingProgress startReading(String userId,String bookId,int totalPages);
	ReadingProgress updateProgress(String userId,String bookId,int currentPage);
	ReadingProgress getProgress(String userId,String bookId);
}
