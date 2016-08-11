package com.johnmalcolmnorwood.hashbash.webapp;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class RainbowTableUniquePasswordJobRunner implements CommandLineRunner {

    @Value("${job.generate.chainLength}")
    private Integer defaultChainLength;

    @Value("${job.generate.charset}")
    private String defaultCharset;

    @Value("${job.generate.hashFunction}")
    private HashFunctionName defaultHashFunctionName;

    @Value("${job.generate.numChains}")
    private Integer defaultNumChains;

    @Value("${job.generate.passwordLength}")
    private Integer defaultPasswordLength;

    @Autowired
    private RainbowTableRepository rainbowTableRepository;

    @Resource(name = "org.springframework.batch.core.Job-uniquePassword")
    private Job rainbowTableUniquePasswordJob;

    @Resource(name = "org.springframework.core.launch.JobLauncher-sync")
    private JobLauncher jobLauncher;

    private int integerValueFromOption(CommandLine commandLine, String option, int defaultValue) {
        return commandLine.getOptionValue(option) != null
                ? Integer.valueOf(commandLine.getOptionValue(option))
                : defaultValue;
    }

    private RainbowTable createRainbowTable(Options options, String[] args) throws ParseException {
        CommandLine commandLine = new DefaultParser().parse(options, args);

        int rainbowChainLength = integerValueFromOption(commandLine, "length", defaultChainLength);
        String rainbowCharset = MoreObjects.firstNonNull(commandLine.getOptionValue("charset"), defaultCharset);
        HashFunctionName rainbowHashFunction = commandLine.getOptionValue("function") != null
                ? HashFunctionName.valueOf(commandLine.getOptionValue("function"))
                : defaultHashFunctionName;

        int rainbowNumChains = integerValueFromOption(commandLine, "num-chains", defaultNumChains);
        int rainbowPasswordLength = integerValueFromOption(commandLine, "pass-len", defaultPasswordLength);

        RainbowTable rainbowTable = RainbowTable.builder()
                .name(commandLine.getOptionValue("rainbow-name"))
                .chainLength(rainbowChainLength)
                .characterSet(rainbowCharset)
                .hashFunction(rainbowHashFunction)
                .numChains(rainbowNumChains)
                .passwordLength(rainbowPasswordLength)
                .build();

        rainbowTableRepository.save(rainbowTable);
        return rainbowTable;
    }

    private Options getOptions() {
        Options options = new Options();
        options.addOption("r", "rainbow-name", true, "Rainbow Table Name");
        options.addOption("l", "length", true, "Specify Chain Length");
        options.addOption("c", "charset", true, "Character Set for passwords");
        options.addOption("f", "function", true, "Hash Function to use");
        options.addOption("n", "num-chains", true, "Number of Chains to generate");
        options.addOption("p", "password-len", true, "Length of Password to use");

        return options;
    }

    @Override
    public void run(String... args) throws Exception {
        RainbowTable rainbowTable = createRainbowTable(getOptions(), args);
        JobParameters jobParameters = new JobParameters(ImmutableMap.of("rainbowTableId", new JobParameter(Long.valueOf(rainbowTable.getId()))));
        jobLauncher.run(rainbowTableUniquePasswordJob, jobParameters);
    }

    public static void main(String... args) {
        new SpringApplicationBuilder()
                .sources(RainbowTableUniquePasswordJobRunner.class, HashbashApplication.class)
                .web(false)
                .run(args);
    }
}
