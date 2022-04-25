package com.example.demo.batch;

import org.springframework.batch.core.step.skip.NonSkippableReadException;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class JobSkipPolicy implements SkipPolicy{

		@Override
		public boolean shouldSkip(Throwable t, int failedCount) throws SkipLimitExceededException {
		// TODO Auto-generated method stub
			System.out.println("------implementing shouldSkip mthd------failedCount---"+failedCount);
			
			
			/*
			 * if(t instanceof NonSkippableReadException){
			 * System.out.println("------implementing NonSkippableReadException---------");
			 * return true; }
			 */
			 
			
		//Scenerio ->If there are 100 records and 50 records are failed or are bad records and 
		// in that scenerio you want to fail a job then you can set below condition that failedCount 
		// reaches the limit return false, as returing false it will fail the job
		//return failedCount>=2?false:true;
		
		//false -> fail the job
		// If you return true, the fault record will not be added and it will continue after that
		return true;
		}
	
}
