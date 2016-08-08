package com.johnmalcolmnorwood.hashbash.job.generate.utils;

import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

public class JobParametersFactory {

    public static JobParameters getParametersForRainbowTableGenerateJob(
            Integer chainLength,
            String charset,
            HashFunctionName hashFunction,
            Integer numChains,
            Integer passwordLength
    ) {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();

        if (chainLength != null) {
            jobParametersBuilder.addLong("chainLength", Long.valueOf(chainLength));
        }

        if (charset != null) {
            jobParametersBuilder.addString("charset", charset);
        }

        if (hashFunction != null) {
            jobParametersBuilder.addString("hashFunction", hashFunction.name());
        }

        if (numChains != null) {
            jobParametersBuilder.addLong("numChains", Long.valueOf(numChains));
        }

        if (passwordLength != null) {
            jobParametersBuilder.addLong("passwordLength", Long.valueOf(passwordLength));
        }

        return jobParametersBuilder.toJobParameters();
    }
}
