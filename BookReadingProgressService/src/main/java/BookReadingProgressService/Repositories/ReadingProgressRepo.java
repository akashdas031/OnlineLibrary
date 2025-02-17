package BookReadingProgressService.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import BookReadingProgressService.Entities.ReadingProgress;

@Repository
public interface ReadingProgressRepo extends JpaRepository<ReadingProgress, String>{

	Optional<ReadingProgress> findByUserIdAndBookId(String userId,String bookId);
}
