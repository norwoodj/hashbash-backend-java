package com.johnmalcolmnorwood.hashbash.api.controller;

import com.johnmalcolmnorwood.hashbash.api.model.GenerateRainbowTableRequest;
import com.johnmalcolmnorwood.hashbash.api.model.SearchResponse;
import com.johnmalcolmnorwood.hashbash.api.service.ApiRainbowTableService;
import com.johnmalcolmnorwood.hashbash.model.HashFunctionName;
import com.johnmalcolmnorwood.hashbash.model.RainbowTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/rainbow-table")
public class RainbowTableController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RainbowTableController.class);

    @Autowired
    private ApiRainbowTableService apiRainbowTableService;

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDuplicate(
            DataIntegrityViolationException ex,
            HttpServletRequest req,
            RedirectAttributes redirectAttributes
    ) {
        String message = String.format("Failed to create rainbow table: %s", ex.getMessage());
        redirectAttributes.addAttribute("error", message);
        return "redirect:/generate-rainbow-table.html";
    }

    @RequestMapping
    public List<RainbowTable> getAll(
            @RequestParam(value = "sortOrder", defaultValue = "DESC") Sort.Direction sortOrder,
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortKey", defaultValue = "") String sortKey
    ) {
        return apiRainbowTableService.getAll(pageNumber, pageSize, sortKey, sortOrder);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> generateJson(@RequestBody GenerateRainbowTableRequest generateRainbowTableRequest) {
        return apiRainbowTableService.generateRainbowTableLocation(generateRainbowTableRequest);
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    public String generateForm(
            GenerateRainbowTableRequest generateRainbowTableRequest,
            RedirectAttributes redirectAttributes
    ) {
        return apiRainbowTableService.generateRainbowTableRedirect(generateRainbowTableRequest, redirectAttributes);
    }

    @RequestMapping(value = "/{rainbowTableId}")
    public ResponseEntity<RainbowTable> get(@PathVariable short rainbowTableId) {
        return apiRainbowTableService.getForId(rainbowTableId);
    }

    @RequestMapping(value = "/{rainbowTableId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable short rainbowTableId) {
        return apiRainbowTableService.deleteRainbowTable(rainbowTableId);
    }

    @RequestMapping("/{rainbowTableId}/search")
    public ResponseEntity<SearchResponse> search(@PathVariable short rainbowTableId, @RequestParam String hash) {
        return apiRainbowTableService.search(rainbowTableId, hash);
    }

    @RequestMapping(value = "/count")
    public Map<String, Long> count() {
        return apiRainbowTableService.getCount();
    }

    @RequestMapping(value = "/hash-functions")
    public List<HashFunctionName> getHashFunctions() {
        return Arrays.asList(HashFunctionName.values());
    }
}
