package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.NonSkippableReadException;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.example.demo.batch.JobSkipPolicy;
import com.example.demo.model.User;
import com.example.demo.model.UserContact;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

	//Creating a Job
    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, //JobBuilderFactory provided by the spring
                   StepBuilderFactory stepBuilderFactory,
                   ItemReader<User> itemReader,
                   ItemProcessor<User, User> itemProcessor, //i/p and o/p is type of User
                   ItemWriter<User> itemWriter, // We have just autowired itemReader itemProcessor itemWriter, yet not provided the implementation
                   ItemWriter<UserContact> itemWriterContact
    ) {
    	
    	//creating Step
        Step step1 = stepBuilderFactory.get("stepOne")//provide name to to stepbuilderfac -> ETL-file-load -> any name can be given
                .<User, User>chunk(1) //we can process in chunks (chunk- means batching), batches of 100
                .reader(itemReader) // reader implmn line 53
                .processor(itemProcessor) // for processor created Processor.java cls
                .writer(itemWriter) // for writer created DBWriter.java cls
                //.faultTolerant().skipPolicy(skipPolicyHanle()) // if we use chunk more than 1 e.g 5-(run 5 chunk at a time) and there is 2 fault records then also it will not add remaining 3 records
                .faultTolerant().skipLimit(3).skip(IllegalStateException.class)
                .retryLimit(2).retry(Exception.class)
                //.taskExecutor(taskExecutor())
                .build();
        
        Step step2 = stepBuilderFactory.get("stepTwo")//provide name to to stepbuilderfac -> ETL-file-load -> any name can be given
                .<UserContact, UserContact>chunk(1) //we can process in chunks (chunk- means batching), batches of 100
                .reader(itemReaderTwo()) 
                .writer(itemWriterContact) // for writer created DBWriter.java cls
                //.faultTolerant().skipPolicy(skipPolicyHandle()) // if we use chunk more than 1 e.g 5-(run 5 chunk at a time) and there is 2 fault records then also it will not add remaining 3 records
                //.faultTolerant().skipLimit(2).skip(IllegalStateException.class).skip(NonSkippableReadException.class)
                //.taskExecutor(taskExecutor())
                .build();

        //creating Job
        return jobBuilderFactory.get("job1") //sequence of ids that we assign for every run, new RunIdIncrementer() -> default provider, u can also use customized one
                .start(step1) //under a job u can have multiple step,if u have only 1 STEP ->then use start(), if multiple -> flow(step) or start(), then next(another step)
                .next(step2)
                .build();
    }

	// Impl of Reader, since we are gonna read csv file we will use inbuilt class -> FlatFileItemReader<return type>
    @Bean
    public FlatFileItemReader<User> itemReader() { 

        FlatFileItemReader<User> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource("src/main/resources/users.csv"));
        flatFileItemReader.setName("CSV-Reader");
        flatFileItemReader.setLinesToSkip(1); //to skip header in csv file
        flatFileItemReader.setLineMapper(lineMapper()); // to map csv to User class we use lineMapper
        return flatFileItemReader;
    }

    @Bean
    public LineMapper<User> lineMapper() {

        DefaultLineMapper<User> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "name", "dept", "salary"); //same name as POJO

        //To set each field of csv (i.e set each row value) to User Pojo
        //There is BeanWrapperFieldSetMapper wch will help in this case
        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);// Just set target type, it will automatically do the mapping from csv to User Pojo

        defaultLineMapper.setLineTokenizer(lineTokenizer); // add tokenizer to linemapper
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }
    
    @Bean
    public TaskExecutor taskExecutor()
    {
    	SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
    	simpleAsyncTaskExecutor.setConcurrencyLimit(5); //how many threads u want to introduce
		return simpleAsyncTaskExecutor;
    }
    
    @Bean
    public JobSkipPolicy skipPolicyHandle()
    {
		return new JobSkipPolicy();
    }
    
    @Bean
    public FlatFileItemReader<UserContact> itemReaderTwo() 
    {
    	 FlatFileItemReader<UserContact> flatFileItemReader = new FlatFileItemReader<>();
         flatFileItemReader.setResource(new FileSystemResource("src/main/resources/users2.csv"));
         flatFileItemReader.setName("CSV-Reader");
         flatFileItemReader.setLinesToSkip(1); //to skip header in csv file
         flatFileItemReader.setLineMapper(lineMapperTwo()); // to map csv to User class we use lineMapper
         return flatFileItemReader;
    }
    
    @Bean
    public LineMapper<UserContact> lineMapperTwo() {

        LineMapper<UserContact> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("name", "number");

        //To set each field of csv (i.e set each row value) to User Pojo
        //There is BeanWrapperFieldSetMapper wch will help in this case
        BeanWrapperFieldSetMapper<UserContact> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(UserContact.class);// Just set target type, it will automatically do the mapping from csv to User Pojo

        ((DefaultLineMapper<UserContact>) defaultLineMapper).setLineTokenizer(lineTokenizer); // add tokenizer to linemapper
        ((DefaultLineMapper<UserContact>) defaultLineMapper).setFieldSetMapper(fieldSetMapper);

        return defaultLineMapper;
    }

}
