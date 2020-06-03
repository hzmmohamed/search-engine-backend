package org.cmpn.searchengine.backend;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
public class TrendsController {
    @GetMapping("api/trends")
    @CrossOrigin
    public List<String> getResults() throws SQLException {
        return new Trends(DBConnect.st).getTrends();
    }
}