package BookReadingProgressService.Entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReadingProgress {

	@Id
	private String progressId;
	private String userId;
	private String bookId;
	private int totalPages;
	private int currentPage;
	private double progressPercentage;
	private LocalDateTime lastReadTime;
	private long totalReadTime;
	private long estimatedTimeRemaining;
	
}
