package BookReadingProgressService.ServiceImplementationClasses;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;

import BookReadingProgressService.Entities.ReadingProgress;
import BookReadingProgressService.Repositories.ReadingProgressRepo;
import BookReadingProgressService.Services.ReadingProgressService;

@Service
public class ReadingProgressServiceImpl implements ReadingProgressService{
	
	private ReadingProgressRepo readingProgressRepo;
	
	private ReadingProgressServiceImpl(ReadingProgressRepo readingProgressRepo){
		this.readingProgressRepo=readingProgressRepo;
	}

	//when a user start reading the book progress will be created 
	@Override
	public ReadingProgress startReading(String userId, String bookId, int totalPages) {
		String progressId = UUID.randomUUID().toString().substring(10).replace("-","").trim();
		ReadingProgress createProgress=ReadingProgress.builder().progressId(progressId)
		   						 .userId(userId)
		   						 .bookId(bookId)
		   						 .totalPages(totalPages)
		   						 .currentPage(0)
		   						 .progressPercentage(0)
		   						 .lastReadTime(LocalDateTime.now())
		   						 .totalReadTime(0)
		   						 .estimatedTimeRemaining(0)
		   						 .build();
		ReadingProgress savedProgress = this.readingProgressRepo.save(createProgress);
		return savedProgress;
	}

	//when a user made changes in page and read another page then the progress will be updated in the database
	@Override
	public ReadingProgress updateProgress(String userId, String bookId, int currentPage) {
		ReadingProgress readingDetails = this.readingProgressRepo.findByUserIdAndBookId(userId, bookId).orElseThrow(()->new RuntimeException("Reading details of the user is not available in the server..."));
		readingDetails.setCurrentPage(currentPage);
		//calculate the percentage of progress
		 double percentage = ((double) currentPage / readingDetails.getTotalPages()) * 100;
		    percentage = Math.round(percentage * 100.0) / 100.0;
		//calculate the time spent and estimated time remaining to complete the book
		    long timeSpent = ChronoUnit.SECONDS.between(readingDetails.getLastReadTime(), LocalDateTime.now());
		    long totalTimeSpent = readingDetails.getTotalReadTime() + timeSpent;
       //estimated time to complete the book
		    // Convert time spent to minutes
		    long totalTimeSpentMinutes = totalTimeSpent / 60;

		    // Calculate estimated time remaining
		    long estimatedRemainingMinutes = 0;
		    if (percentage > 0) {
		        estimatedRemainingMinutes = (long) ((100 - percentage) / percentage * totalTimeSpentMinutes);
		    }
		 
		readingDetails.setProgressPercentage(percentage);
		readingDetails.setLastReadTime(LocalDateTime.now());
		readingDetails.setTotalReadTime(readingDetails.getEstimatedTimeRemaining()+estimatedRemainingMinutes);
		readingDetails.setEstimatedTimeRemaining(estimatedRemainingMinutes);
		ReadingProgress updatedReadingDetails = this.readingProgressRepo.save(readingDetails);
		return updatedReadingDetails;
	}

	//to view the progress details of the user like how much progress he has made for reading the book
	@Override
	public ReadingProgress getProgress(String userId, String bookId) {
		ReadingProgress readingDetails = this.readingProgressRepo.findByUserIdAndBookId(userId, bookId).orElseThrow(()-> new RuntimeException("Reading details is not available on the server"));
		return readingDetails;
	}

}
