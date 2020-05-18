package org.cmpn.searchengine.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class SearchController {


    @GetMapping("/search/web")
    public  ArrayList<Document> results(@RequestParam(value = "query", defaultValue = "") String query,
                                        @RequestParam(value = "page", defaultValue = "1") String page) {
        ArrayList<Document> results = new ArrayList<Document>();
        results.add(new Document("Wikipedia", "www.wikipedia.com", "This is wikipedia"));
        results.add(new Document("Brain Pickings", "www.brainpickings.com", "A decade of excellent writing"));
        results.add(new Document(query + " site", "www.query.com", "pageParam = " + page));

        return results;
    }
}