package com.jpmc.midascore.component;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionListener {
	private final TransactionService transactionService;

	public TransactionListener(TransactionService ts){
		this.transactionService = ts;
	}

	@KafkaListener(topics = "${general.kafka-topic}")
	public void listen(Transaction transaction){
		transactionService.handleTransaction(transaction);
	}
}
