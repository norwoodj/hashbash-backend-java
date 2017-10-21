package com.johnmalcolmnorwood.hashbash.api.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.johnmalcolmnorwood.hashbash.api.model.GenerateRainbowTableRequest;
import com.johnmalcolmnorwood.hashbash.api.model.SearchResponse;
import com.johnmalcolmnorwood.hashbash.api.utils.EntityResponseUtils;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.model.RainbowTableSearch;
import com.johnmalcolmnorwood.hashbash.model.RainbowTableSearchStatus;
import com.johnmalcolmnorwood.hashbash.mq.message.RainbowTableGenerateRequestMessage;
import com.johnmalcolmnorwood.hashbash.mq.message.RainbowTableSearchRequestMessage;
import com.johnmalcolmnorwood.hashbash.producer.HashbashMqPublishingService;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

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
    private RainbowTableRepository rainbowTableRepository;

    @Autowired
    private RainbowTableSearchRepository rainbowTableSearchRepository;

    @Autowired
    private HashbashMqPublishingService hashbashMqPublishingService;


    private static <T> T firstNonNull(T one, T two) {
        return one == null ? two : one;
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

    private RainbowTable createRainbowTable(GenerateRainbowTableRequest generateRainbowTableRequest) {
        int rainbowChainLength = firstNonNull(generateRainbowTableRequest.getChainLength() , defaultChainLength);
        String rainbowCharset = firstNonNull(generateRainbowTableRequest.getCharset(), defaultCharset);
        HashFunctionName rainbowHashFunction = firstNonNull(generateRainbowTableRequest.getHashFunction(), defaultHashFunctionName);
        int rainbowNumChains = firstNonNull(generateRainbowTableRequest.getNumChains(), defaultNumChains);
        int rainbowPasswordLength = firstNonNull(generateRainbowTableRequest.getPasswordLength(), defaultPasswordLength);

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

    private void requestRainbowTableGenerate(RainbowTable rainbowTable) {
        RainbowTableGenerateRequestMessage rainbowTableGenerateRequestMessage = RainbowTableGenerateRequestMessage.builder()
                .rainbowTableId(rainbowTable.getId())
                .build();

        hashbashMqPublishingService.sendRainbowTableGenerateRequestMessage(rainbowTableGenerateRequestMessage);
    }

    public ResponseEntity<Void> generateRainbowTableLocation(GenerateRainbowTableRequest generateRainbowTableRequest) {
        RainbowTable rainbowTable = createRainbowTable(generateRainbowTableRequest);
        requestRainbowTableGenerate(rainbowTable);
        return EntityResponseUtils.getResponseForCreatedEntity(rainbowTable.getId());
    }

    public ModelAndView generateRainbowTableRedirect(GenerateRainbowTableRequest generateRainbowTableRequest) {
        RainbowTable rainbowTable = createRainbowTable(generateRainbowTableRequest);
        requestRainbowTableGenerate(rainbowTable);

        ModelAndView redirect = new ModelAndView("redirect:/search-rainbow-table");
        redirect.getModelMap().addAttribute("rainbowTableId", rainbowTable.getId());
        return redirect;
    }

    private RainbowTableSearch createRainbowTableSearchDatabaseObject(short rainbowTableId, String hash) {
        RainbowTableSearch rainbowTableSearch = RainbowTableSearch.builder()
                .hash(hash)
                .status(RainbowTableSearchStatus.QUEUED)
                .rainbowTableId(rainbowTableId)
                .build();

        return rainbowTableSearchRepository.save(rainbowTableSearch);
    }

    private void sendSearchRequestMqMessage(short rainbowTableId, long searchId, String hash) {
        RainbowTableSearchRequestMessage rainbowTableSearchRequestMessage = RainbowTableSearchRequestMessage.builder()
                .hash(hash)
                .searchId(searchId)
                .rainbowTableId(rainbowTableId)
                .build();

        hashbashMqPublishingService.sendRainbowTableSearchRequestMessage(rainbowTableSearchRequestMessage);
    }

    public ResponseEntity<SearchResponse> search(short rainbowTableId, String hash) {
        RainbowTable rainbowTable = rainbowTableRepository.findOne(rainbowTableId);

        if (rainbowTable == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        RainbowTableSearch rainbowTableSearch = createRainbowTableSearchDatabaseObject(rainbowTableId, hash);
        sendSearchRequestMqMessage(rainbowTableId, rainbowTableSearch.getId(), hash);

        SearchResponse searchResponse = SearchResponse.builder()
                .hash(hash)
                .status(rainbowTableSearch.getStatus())
                .searchId(rainbowTableSearch.getId())
                .build();

        return ResponseEntity.accepted().body(searchResponse);
    }

    public List<RainbowTableSearch> getSearchesForRainbowTable(
            short rainbowTableId,
            int pageNumber,
            int pageSize,
            String sortKey,
            Sort.Direction sortOrder,
            boolean showNotFound
    ) {
        String defaultedSortKey = StringUtils.isEmpty(sortKey) ? "id" : sortKey;
        Pageable pageRequest = new PageRequest(pageNumber, pageSize, new Sort(sortOrder, defaultedSortKey));

        if (showNotFound) {
            return rainbowTableSearchRepository.getAllByRainbowTableId(rainbowTableId, pageRequest);
        }

        return rainbowTableSearchRepository.getAllByRainbowTableIdAndStatusIn(
                rainbowTableId,
                Sets.newHashSet(RainbowTableSearchStatus.QUEUED, RainbowTableSearchStatus.STARTED, RainbowTableSearchStatus.FOUND),
                pageRequest
        );
    }
}
