package com.jpmc.midascore.controller;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BalanceController {

	private final UserRepository userRepository;

	public BalanceController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/balance")
	private Balance getBalance(@RequestParam(value = "userId") String userID){
		Optional<UserRecord> userOptional = Optional.ofNullable(userRepository.findById(Long.parseLong(userID)));

		if(userOptional.isEmpty()){
			return new Balance(0);
		}

		UserRecord user = userOptional.get();
		return new Balance(user.getBalance());
	}
}
