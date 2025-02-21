package UserService.Services;

import java.time.LocalDateTime;

public interface BlackListedTokenService {

	void addToBlackList(String token,LocalDateTime expiryDate);
	boolean isBlackListed(String token);
	void removeExpiredTokens();
}
