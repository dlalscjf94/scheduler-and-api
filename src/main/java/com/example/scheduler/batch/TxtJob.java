package com.example.scheduler.batch;


import com.example.scheduler.dto.FiledataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

/*
* .txt 파일에 대한 batch 작업을 진행*/
@RequiredArgsConstructor
@Slf4j
@Configuration
public class TxtJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    // 24시간 data row 24 => chunksize 24 고정
    private static final int chunksize = 24;

    @Bean
    public Job textJob_batchBuild(){
        return jobBuilderFactory.get("TxtJob")
                .start(textJob_batchStep())
                .build();
    }

    @Bean
    public Step textJob_batchStep(){
        return stepBuilderFactory.get("textJob_batchStep")
                .<FiledataDto, FiledataDto>chunk(chunksize)
                .reader(txtJob_FileReader())
                .writer(FiledataDto -> FiledataDto.stream().forEach(obj -> {
                    log.info(obj.toString());
                })).build();
    }

    @Bean
    public FlatFileItemReader<FiledataDto> txtJob_FileReader() {
        FlatFileItemReader<FiledataDto> txtFileItemReader = new FlatFileItemReader<>();
        txtFileItemReader.setResource(new ClassPathResource("/file2.txt"));

        DefaultLineMapper<FiledataDto> filedataDtoDefaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter("|");
        delimitedLineTokenizer.setNames("data_time", "subs_num", "resign_num", "pay_amount", "used_amount", "sales_amount");

        BeanWrapperFieldSetMapper<FiledataDto> beanWrapperFieldSetMapper =  new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(FiledataDto.class);
        filedataDtoDefaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        filedataDtoDefaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        txtFileItemReader.setLineMapper(filedataDtoDefaultLineMapper);

        return txtFileItemReader;
    }
}
