package com.johnmalcolmnorwood.hashbash.api.service;

import com.google.common.collect.ImmutableMap;
import com.johnmalcolmnorwood.hashbash.api.model.GenerateRainbowTableRequest;
import com.johnmalcolmnorwood.hashbash.api.model.SearchResponse;
import com.johnmalcolmnorwood.hashbash.api.utils.EntityResponseUtils;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import com.johnmalcolmnorwood.hashbash.model.RainbowTableSearch;
import com.johnmalcolmnorwood.hashbash.model.RainbowTableSearchStatus;
import com.johnmalcolmnorwood.hashbash.mq.message.RainbowTableActionRequestMessage;
import com.johnmalcolmnorwood.hashbash.mq.message.RainbowTableSearchRequestMessage;
import com.johnmalcolmnorwood.hashbash.producer.HashbashMqPublishingService;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableRepository;
import com.johnmalcolmnorwood.hashbash.repository.RainbowTableSearchRepository;
import com.johnmalcolmnorwood.hashbash.repository.model.RainbowTableSearchResults;
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
import java.util.stream.Collectors;

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
        return EntityResponseUtils.getResponseForGetEntity(rainbowTableRepository.findById(rainbowTableId));
    }

    public Map<String, Long> getCount() {
        return ImmutableMap.of("rainbowTableCount", rainbowTableRepository.count());
    }

    public ResponseEntity<Void> deleteRainbowTable(short rainbowTableId) {
        if (rainbowTableRepository.findById(rainbowTableId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var deleteRequest = RainbowTableActionRequestMessage.builder()
                .rainbowTableId(rainbowTableId)
                .build();

        hashbashMqPublishingService.sendRainbowTableDeleteRequestMessage(deleteRequest);
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
        RainbowTableActionRequestMessage rainbowTableActionRequestMessage = RainbowTableActionRequestMessage.builder()
                .rainbowTableId(rainbowTable.getId())
                .build();

        hashbashMqPublishingService.sendRainbowTableGenerateRequestMessage(rainbowTableActionRequestMessage);
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
        var rainbowTable = rainbowTableRepository.findById(rainbowTableId);

        if (rainbowTable.isEmpty()) {
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
            boolean includeNotFound
    ) {
        String defaultedSortKey = StringUtils.isEmpty(sortKey) ? "id" : sortKey;
        Pageable pageRequest = new PageRequest(pageNumber, pageSize, new Sort(sortOrder, defaultedSortKey));

        if (includeNotFound) {
            return rainbowTableSearchRepository.getAllByRainbowTableId(rainbowTableId, pageRequest);
        }

        return rainbowTableSearchRepository.getAllByRainbowTableIdAndStatusNot(
                rainbowTableId,
                RainbowTableSearchStatus.NOT_FOUND,
                pageRequest
        );
    }

    public long getSearchCountForRainbowTable(short rainbowTableId, boolean includeNotFound) {
        if (includeNotFound) {
            return rainbowTableSearchRepository.countByRainbowTableId(rainbowTableId);
        }

        return rainbowTableSearchRepository.countByRainbowTableIdAndStatusNot(
                rainbowTableId,
                RainbowTableSearchStatus.NOT_FOUND
        );
    }

    public Map<String, Long> getSearchResultsForRainbowTable(short rainbowTableId) {
        List<RainbowTableSearchResults> searchCountByStatus = rainbowTableSearchRepository.searchCountsByStatus(rainbowTableId);

        long totalSearches = searchCountByStatus.stream()
                .map(RainbowTableSearchResults::getCount)
                .mapToLong(Long::valueOf)
                .sum();

        long foundSearches = searchCountByStatus.stream()
                .filter(s -> s.getSearchStatus().equals(RainbowTableSearchStatus.FOUND))
                .findFirst()
                .map(RainbowTableSearchResults::getCount)
                .orElse(0L);

        return ImmutableMap.of(
                "totalSearches", totalSearches,
                "foundSearches", foundSearches
        );
    }

    public RainbowTableSearch getSearchForId(long rainbowTableSearchId) {
        return rainbowTableSearchRepository.findById(rainbowTableSearchId)
                .orElse(null);
    }
}
