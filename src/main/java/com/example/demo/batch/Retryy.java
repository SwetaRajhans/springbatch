package com.example.demo.batch;

import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.SimpleRetryPolicy;

public class Retryy extends SimpleRetryPolicy {

		@Override
		public boolean canRetry(RetryContext context) {
		// TODO Auto-generated method stub
		return true;
		}

}
