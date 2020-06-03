package org.cmpn.searchengine.backend;

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
        ArrayList<String> suggestions = new ArrayList<String>();
        suggestions.add("one");
        suggestions.add("two");
        suggestions.add("three");
        return suggestions;
    }
}