package com.johnmalcolmnorwood.hashbash.api.controller;

import com.google.common.base.MoreObjects;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.johnmalcolmnorwood.hashbash.api.model.GenerateRainbowTableRequest;
import com.johnmalcolmnorwood.hashbash.api.model.SearchResponse;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunctions;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamilies;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamily;
import com.johnmalcolmnorwood.hashbash.rainbow.service.RainbowChainGeneratorService;
import com.johnmalcolmnorwood.hashbash.rainbow.service.RainbowTableSearchService;
import com.johnmalcolmnorwood.hashbash.repository.RainbowChainRepository;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/rainbow-table")
public class RainbowTableController {

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
    private RainbowChainRepository rainbowChainRepository;

    @Autowired
    private RainbowTableRepository rainbowTableRepository;


    @Resource(name = "org.springframework.core.launch.JobLauncher-async")
    private JobLauncher jobLauncher;

    @Resource(name = "org.springframework.batch.core.Job-generate")
    private Job rainbowTableGenerateJob;


    @RequestMapping(value = "/hash-functions")
    public List<HashFunctionName> getHashFunctions() {
        return Arrays.asList(HashFunctionName.values());
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<RainbowTable> getAll() {
        return rainbowTableRepository.findAll();
    }

    @RequestMapping(value = "/{rainbowTableId}", method = RequestMethod.GET)
    public ResponseEntity<RainbowTable> get(@PathVariable short rainbowTableId) {
        RainbowTable rainbowTable = rainbowTableRepository.findOne(rainbowTableId);
        if (rainbowTable == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(rainbowTable);
    }

    @RequestMapping(value = "/{rainbowTableId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable short rainbowTableId) {
        rainbowTableRepository.delete(rainbowTableId);
        URI rainbowTableUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/api/rainbow-table/{id}")
                .buildAndExpand(rainbowTableId)
                .toUri();

        return ResponseEntity.noContent()
                .location(rainbowTableUri)
                .build();
    }

    private void handleGenerate(RainbowTable rainbowTable) {
        try {
            JobParameters jobParameters = new JobParameters(ImmutableMap.of("rainbowTableId", new JobParameter(Long.valueOf(rainbowTable.getId()))));
            jobLauncher.run(rainbowTableGenerateJob, jobParameters);
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException | JobParametersInvalidException | JobRestartException e) {
            Throwables.propagate(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST, headers = "content-type=application/x-www-form-urlencoded")
    public String generateFromForm(
            GenerateRainbowTableRequest generateRainbowTableRequest
    ) {
        RainbowTable rainbowTable = createRainbowTable(generateRainbowTableRequest);
        handleGenerate(rainbowTable);
        return "redirect:/tables.html";
    }

    @RequestMapping(method = RequestMethod.POST, headers = "content-type=application/json")
    public ResponseEntity<Void> generateFromJson(@RequestBody GenerateRainbowTableRequest generateRainbowTableRequest) {
        RainbowTable rainbowTable = createRainbowTable(generateRainbowTableRequest);
        URI rainbowTableUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(rainbowTable.getId())
                .toUri();

        return ResponseEntity.created(rainbowTableUri).build();
    }

    @RequestMapping("/{rainbowTableId}/search")
    public ResponseEntity<SearchResponse> search(
            @PathVariable short rainbowTableId,
            @RequestParam String hash
    ) {
        RainbowTable rainbowTable = rainbowTableRepository.findOne(rainbowTableId);
        if (rainbowTable == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ReductionFunctionFamily reductionFunctionFamily = ReductionFunctionFamilies.defaultReductionFunctionFamily(
                rainbowTable.getPasswordLength(),
                rainbowTable.getCharacterSet()
        );

        RainbowChainGeneratorService rainbowChainGeneratorService = new RainbowChainGeneratorService(
                HashFunctions.getHashFunctionByName(rainbowTable.getHashFunction()),
                reductionFunctionFamily
        );

        RainbowTableSearchService rainbowTableSearchService = new RainbowTableSearchService(
                rainbowChainGeneratorService,
                rainbowChainRepository,
                rainbowTableId,
                rainbowTable.getChainLength()
        );

        String password = rainbowTableSearchService.reverseHash(HashCode.fromString(hash));
        SearchResponse searchResponse = SearchResponse.builder()
                .hash(hash)
                .password(password)
                .build();

        if (password == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(searchResponse);
    }

    private RainbowTable createRainbowTable(GenerateRainbowTableRequest generateRainbowTableRequest) {
        int rainbowChainLength = MoreObjects.firstNonNull(generateRainbowTableRequest.getChainLength(), defaultChainLength);
        String rainbowCharset = MoreObjects.firstNonNull(generateRainbowTableRequest.getCharset(), defaultCharset);
        HashFunctionName rainbowHashFunction = MoreObjects.firstNonNull(generateRainbowTableRequest.getHashFunction(), defaultHashFunctionName);
        int rainbowNumChains = MoreObjects.firstNonNull(generateRainbowTableRequest.getNumChains(), defaultNumChains);
        int rainbowPasswordLength = MoreObjects.firstNonNull(generateRainbowTableRequest.getPasswordLength(), defaultPasswordLength);

        RainbowTable rainbowTable = RainbowTable.builder()
                .name(generateRainbowTableRequest.getName())
                .chainLength(rainbowChainLength)
                .characterSet(rainbowCharset)
                .hashFunction(rainbowHashFunction)
                .numChains(rainbowNumChains)
                .passwordLength(rainbowPasswordLength)
                .build();

        return rainbowTableRepository.save(rainbowTable);
    }
}
