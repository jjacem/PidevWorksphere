package esprit.tn.entities;

import java.util.List;
import java.util.Map;

public class ChartData {
    private String title;
    private List<Map<String, Object>> data;

    public ChartData(String title, List<Map<String, Object>> data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }
}