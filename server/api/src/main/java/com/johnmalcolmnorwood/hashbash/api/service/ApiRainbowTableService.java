package com.johnmalcolmnorwood.hashbash.api.service;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.johnmalcolmnorwood.hashbash.api.model.GenerateRainbowTableRequest;
import com.johnmalcolmnorwood.hashbash.api.model.SearchResponse;
import com.johnmalcolmnorwood.hashbash.api.utils.EntityResponseUtils;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.rainbow.function.HashFunctions;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamilies;
import com.johnmalcolmnorwood.hashbash.rainbow.function.ReductionFunctionFamily;
import com.johnmalcolmnorwood.hashbash.rainbow.service.RainbowChainGeneratorService;
import com.johnmalcolmnorwood.hashbash.rainbow.service.RainbowTableSearchService;
import com.johnmalcolmnorwood.hashbash.repository.RainbowChainRepository;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.batch.operations.JobRestartException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ApiRainbowTableService {

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
    private RainbowChainRepository rainbowChainRepository;

    @Autowired
    private RainbowTableRepository rainbowTableRepository;


    @Resource(name = "org.springframework.core.launch.JobLauncher-async")
    private JobLauncher jobLauncher;

    @Resource(name = "org.springframework.batch.core.Job-generate")
    private Job rainbowTableGenerateJob;


    private void startGenerateTableJob(RainbowTable rainbowTable) {
        try {
            JobParameters jobParameters = new JobParameters(ImmutableMap.of("rainbowTableId", new JobParameter(Long.valueOf(rainbowTable.getId()))));
            jobLauncher.run(rainbowTableGenerateJob, jobParameters);
        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException | JobParametersInvalidException | JobRestartException e) {
            throw new RuntimeException(e);
        } catch (org.springframework.batch.core.repository.JobRestartException e) {
            throw new RuntimeException(e);
        }
    }

    private RainbowTable handleGenerateRainbowTable(GenerateRainbowTableRequest generateRainbowTableRequest) {
        RainbowTable rainbowTable = createRainbowTable(generateRainbowTableRequest);
        startGenerateTableJob(rainbowTable);
        return rainbowTable;
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

    public List<RainbowTable> getAll(int pageNumber, int limit, String sortKey, Sort.Direction sortOrder) {
        String defaultedSortKey = StringUtils.isEmpty(sortKey) ? "id" : sortKey;
        Pageable pageRequest = new PageRequest(pageNumber, limit, new Sort(sortOrder, defaultedSortKey));
        return rainbowTableRepository.findAll(pageRequest).getContent();
    }

    public ResponseEntity<RainbowTable> getForId(short rainbowTableId) {
        return EntityResponseUtils.getResponseForGetEntity(rainbowTableRepository.findOne(rainbowTableId));
    }

    public Map<String, Long> getCount() {
        return ImmutableMap.of("rainbowTableCount", rainbowTableRepository.count());
    }

    public ResponseEntity<Void> deleteRainbowTable(short rainbowTableId) {
        if (rainbowTableRepository.findOne(rainbowTableId) == null) {
            return ResponseEntity.notFound().build();
        }

        CompletableFuture.runAsync(() -> rainbowTableRepository.delete(rainbowTableId));
        return EntityResponseUtils.getResponseForDeleteEntity();
    }

    public ResponseEntity<Void> generateRainbowTableLocation(GenerateRainbowTableRequest generateRainbowTableRequest) {
        RainbowTable rainbowTable = handleGenerateRainbowTable(generateRainbowTableRequest);
        return EntityResponseUtils.getResponseForCreatedEntity(rainbowTable.getId());
    }

    public String generateRainbowTableRedirect(
            GenerateRainbowTableRequest generateRainbowTableRequest,
            RedirectAttributes redirectAttributes
    ) {
        handleGenerateRainbowTable(generateRainbowTableRequest);
        return "redirect:/rainbow-tables.html";
    }

    public ResponseEntity<SearchResponse> search(short rainbowTableId, String hash) {
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
}
