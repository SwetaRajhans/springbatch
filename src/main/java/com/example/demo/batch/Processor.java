package com.example.demo.batch;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.example.demo.model.User;

@Component
public class Processor implements ItemProcessor<User, User> {

	private static final Map<String, String> DEPT_NAMES = new HashMap<>();

	private int count = 1;

	public Processor() {
		DEPT_NAMES.put("11", "Technology");
		DEPT_NAMES.put("12", "Operations");
		DEPT_NAMES.put("13", "Accounts");
		DEPT_NAMES.put("14", "HR");
	}

	@Override
	@Retryable(value = RuntimeException.class, maxAttempts = 5, backoff = @Backoff(5000))
	public User process(User user) throws Exception {
		// performing some processing like transform deptcode to dept desc, we can
		// perform any processing as per req
		String deptCode = user.getDept();
		String dept = DEPT_NAMES.get(deptCode);
		user.setDept(deptCode);
		user.setTime(new Date());
		// System.out.println(String.format("Converted from [%s] to [%s]", deptCode,
		// dept));

		System.out.println("Retry Count---->" + (count++));
		// can be rest template call
		throw new RuntimeException();
		// return user;
	}

	@Recover
	public String recover(RuntimeException exception) {
		System.out.println(
				"--------------------Recover called---------------------------------" + exception.getMessage());
		return null;
	}
}
