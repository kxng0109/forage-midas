package com.jpmc.midascore.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TransactionRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id", nullable = false)
	private UserRecord sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recipient_id", nullable = false)
	private UserRecord recipient;

	@Column(nullable = false)
	private float amount;

	@Column(nullable = false)
	private float incentiveAmount;

	@Column(nullable = false)
	private LocalDateTime timestamp;

	public TransactionRecord() {
	}

	public TransactionRecord(UserRecord sender, UserRecord recipient, float amount, float ia) {
		this.sender = sender;
		this.recipient = recipient;
		this.amount = amount;
		this.incentiveAmount = ia;
		this.timestamp = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "TransactionRecord{" +
				"id=" + id +
				", sender=" + sender +
				", recipient=" + recipient +
				", amount=" + amount +
				", timestamp=" + timestamp +
				'}';
	}
}
