package org.cmpn.searchengine.backend.autocomplete;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class AutoCompleteController {

    @GetMapping("/api/autocomplete")
    @CrossOrigin
    public ArrayList<String> getResults(@RequestParam(value = "q", defaultValue = "") String query ) {

        System.out.println("/autocomplete Route received a request with query:" + query);
        ArrayList<String> suggestions = new ArrayList<>();
        suggestions.add("one");
        suggestions.add("two");
        suggestions.add("three");
        return suggestions;
    }
}