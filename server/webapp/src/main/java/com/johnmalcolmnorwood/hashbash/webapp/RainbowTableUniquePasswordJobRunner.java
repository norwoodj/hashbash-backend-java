package com.johnmalcolmnorwood.hashbash.webapp;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
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

import javax.annotation.Resource;


public class RainbowTableUniquePasswordJobRunner implements CommandLineRunner {

    @Value("${hashbash.rainbow.default.chainLength}")
    private Integer defaultChainLength;

    @Value("${hashbash.rainbow.default.charset}")
    private String defaultCharset;

    @Value("${hashbash.rainbow.default.hashFunction}")
    private HashFunctionName defaultHashFunctionName;

    @Value("${hashbash.rainbow.default.numChains}")
    private Integer defaultNumChains;

    @Value("${hashbash.rainbow.default.passwordLength}")
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

    private RainbowTable createRainbowTable(CommandLine commandLine) throws ParseException {
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
        options.addOption("h", "help", false, "Print this help message and exit");

        return options;
    }

    @Override
    public void run(String... args) throws Exception {
        Options options = getOptions();
        CommandLine commandLine = new DefaultParser().parse(options, args);

        if (commandLine.hasOption("help")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("rainbow-table-unique-password-job", options);
            return;
        }

        RainbowTable rainbowTable = createRainbowTable(commandLine);
        JobParameters jobParameters = new JobParameters(ImmutableMap.of("rainbowTableId", new JobParameter(Long.valueOf(rainbowTable.getId()))));
        jobLauncher.run(rainbowTableUniquePasswordJob, jobParameters);
        System.exit(0);
    }

    public static void main(String... args) {
        new SpringApplicationBuilder()
                .sources(RainbowTableUniquePasswordJobRunner.class, HashbashApplication.class)
                .web(false)
                .run(args);
    }
}
