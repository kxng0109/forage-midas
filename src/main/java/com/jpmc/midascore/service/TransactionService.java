package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class TransactionService {
	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
	private final UserRepository userRepository;
	private final TransactionRepository transactionRepository;
	private final RestTemplate restTemplate;

	private final String INCENTIVE_URL = "http://localhost:8080/incentive";

	public TransactionService(UserRepository ur, TransactionRepository tr, RestTemplate rt) {
		this.userRepository = ur;
		this.transactionRepository = tr;
		this.restTemplate = rt;
	}

	@Transactional
	public void handleTransaction(Transaction transaction) {
		Optional<UserRecord> senderOptional = Optional.ofNullable(userRepository.findById(transaction.getSenderId()));
		Optional<UserRecord> recipientOptional = Optional.ofNullable(userRepository.findById(transaction.getRecipientId()));

		if (senderOptional.isEmpty() || recipientOptional.isEmpty()) {
			logger.warn("Sender or recipient not found! Discarding. {}", transaction);
			return;
		}

		UserRecord sender = senderOptional.get();
		UserRecord recipient = recipientOptional.get();

		Incentive incentive;
		try{
			incentive = restTemplate.postForObject(INCENTIVE_URL, transaction, Incentive.class);
		} catch (Exception e) {
			logger.error("An error occurred while fetching the incentive API. Using 0 as the value for the incentives. {}", e.getMessage());
			incentive = new Incentive(0);
		}

		if (sender.getBalance() < transaction.getAmount()) {
			logger.warn("Insufficient balance for sender with id of {}. Cannot complete transaction. {}", sender.getId(), transaction);
			return;
		}

		sender.setBalance(sender.getBalance() - transaction.getAmount());
		recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentive.getAmount());

		userRepository.save(sender);
		userRepository.save(recipient);

		TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentive.getAmount());
		transactionRepository.save(record);

		logger.info("Successfully processed and recorded transaction: {}", transaction);
		logger.info("Sender's balance: {} \n Recipient's balance: {}", sender.getBalance(), recipient.getBalance());
	}
}
