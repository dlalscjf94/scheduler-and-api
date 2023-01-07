package com.example.scheduler.batch;


import com.example.scheduler.dto.FiledataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.activation.DataSource;


@RequiredArgsConstructor
@Slf4j
@Configuration
public class CsvJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource;
    // 24시간 data row 24 => chunksize 24 고정
    private static final int chunksize = 24;

    @Bean
    public Job csvJob_batchBuild() {
        return jobBuilderFactory.get("csvJob")
                .start(csvJob_batchStep())
                .build();
    }

    @Bean
    public Step csvJob_batchStep() {
        return stepBuilderFactory.get("csvJob_batchStep")
                .<FiledataDto, FiledataDto>chunk(chunksize)
                .reader(csvJob_FileReader())
                .writer(filedataDto -> filedataDto.stream().forEach(obj -> {
                    log.info(obj.toString());
//                .writer(csvJob_FileWriter())
                })).build();
    }

    @Bean
    public FlatFileItemReader<FiledataDto> csvJob_FileReader(){
        FlatFileItemReader<FiledataDto> csvFileItemReader =  new FlatFileItemReader<>();
        csvFileItemReader.setResource(new ClassPathResource("/file1.csv"));
        // 첫번째 라인 skip => col명
        csvFileItemReader.setLinesToSkip(1);
        DefaultLineMapper<FiledataDto> dtoDefaultLineMapper = new DefaultLineMapper<>();
        // 구분자
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        // col 명 set
        delimitedLineTokenizer.setNames("data_time", "subs_num", "resign_num", "pay_amount", "used_amount", "sales_amount");
        // csv 구분자 : ,
        delimitedLineTokenizer.setDelimiter(",");

        BeanWrapperFieldSetMapper<FiledataDto> beanWrapperFieldSetMapper =  new BeanWrapperFieldSetMapper<>();
        beanWrapperFieldSetMapper.setTargetType(FiledataDto.class);
        dtoDefaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
        dtoDefaultLineMapper.setFieldSetMapper(beanWrapperFieldSetMapper);
        csvFileItemReader.setLineMapper(dtoDefaultLineMapper);

        return csvFileItemReader;
    }

//    @Bean
//    public FlatFileItemWriter<FiledataDto> csvJob_FileWriter() {
//
//        return new FlatFileItemWriterBuilder<FiledataDto>()
//                .dataSource(dataSource)
//                .sql("insert into filedata(data_time, subs_num, resign_num, pay_amount, used_amount, sales_amount) values (:data_time, :subs_num, :resign_num, :pay_amount, :used_amount, :sales_amount)")
//                .beanMapped()
//                .build();
//    }


}
