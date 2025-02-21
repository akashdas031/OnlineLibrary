package UserService.ServiceImpls;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import UserService.Entities.BlackListedToken;
import UserService.Repositories.BlackListedTokenRepository;
import UserService.Services.BlackListedTokenService;
import jakarta.transaction.Transactional;

@Service
public class BlackListedTokenServiceImpl implements BlackListedTokenService{
	
	private BlackListedTokenRepository blackListedTokenRepo;
	
	public BlackListedTokenServiceImpl(BlackListedTokenRepository blackListedTokenRepo) {
		this.blackListedTokenRepo=blackListedTokenRepo;
	}

	@Override
	@Transactional
	public void addToBlackList(String token,LocalDateTime expiryDate) {
		if(!isBlackListed(token)) {
			String blockId = UUID.randomUUID().toString().substring(10).replaceAll("-", "").trim();
			BlackListedToken blacklisted = BlackListedToken.builder().blackListTokenId(blockId).token(token)
									  .expiryDate(expiryDate)
									  .build();
			blackListedTokenRepo.save(blacklisted);
		}
		
	}

	@Override
	public boolean isBlackListed(String token) {
		return blackListedTokenRepo.existsByToken(token);
	}

	@Override
	@Transactional
	@Scheduled(fixedRate = 86400000)
	public void removeExpiredTokens() {
		blackListedTokenRepo.deleteByExpiryDate(LocalDateTime.now());
		
	}

}
