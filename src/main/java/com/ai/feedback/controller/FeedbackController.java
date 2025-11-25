package com.ai.feedback.controller;

import com.ai.feedback.entity.FeedbackEntity;
import com.ai.feedback.model.FeedbackRequest;
import com.ai.feedback.repository.FeedbackRepository;
import com.ai.feedback.service.SentimentAnalyzer;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class FeedbackController {

  private final FeedbackRepository repo;
  private final SentimentAnalyzer analyzer = new SentimentAnalyzer();

  public FeedbackController(FeedbackRepository repo) {
    this.repo = repo;
  }

  @PostMapping("/feedback")
  public ResponseEntity<FeedbackEntity> saveFeedback(@RequestBody FeedbackRequest req) {
    double score = analyzer.score(req.getText());
    String label = analyzer.label(score);
    FeedbackEntity e = new FeedbackEntity();
    e.setStudentName(req.getStudentName());
    e.setText(req.getText());
    e.setCategory(req.getCategory());
    e.setSentimentLabel(label);
    e.setSentimentScore(score);
    e.setCreatedAt(LocalDateTime.now());
    FeedbackEntity saved = repo.save(e);
    return ResponseEntity.ok(saved);
  }

  @GetMapping("/feedbacks")
  public List<FeedbackEntity> listAll() {
    return repo.findAll();
  }

  @GetMapping("/stats")
  public Map<String,Object> stats() {
    List<FeedbackEntity> all = repo.findAll();
    long positive = all.stream().filter(f -> "Positive".equals(f.getSentimentLabel())).count();
    long neutral = all.stream().filter(f -> "Neutral".equals(f.getSentimentLabel())).count();
    long negative = all.stream().filter(f -> "Negative".equals(f.getSentimentLabel())).count();
    Map<String,Object> m = new HashMap<>();
    m.put("positive", positive);
    m.put("neutral", neutral);
    m.put("negative", negative);
    m.put("total", all.size());
    Map<String, Long> catCounts = all.stream().collect(Collectors.groupingBy(f -> f.getCategory() == null ? "Others" : f.getCategory(), Collectors.counting()));
    m.put("categoryCounts", catCounts);
    Map<String, Map<String, Long>> catSent = new HashMap<>();
    for (FeedbackEntity f: all) {
      String c = f.getCategory() == null ? "Others" : f.getCategory();
      catSent.putIfAbsent(c, new HashMap<>());
      Map<String, Long> inner = catSent.get(c);
      inner.put(f.getSentimentLabel(), inner.getOrDefault(f.getSentimentLabel(), 0L) + 1);
    }
    m.put("categorySentiment", catSent);
    Map<String, Long> byDate = all.stream().collect(Collectors.groupingBy(
      f -> f.getCreatedAt().toLocalDate().toString(), Collectors.counting()
    ));
    m.put("byDate", byDate);
    return m;
  }

  @GetMapping("/recommendations")
  public List<String> recommendations() {
    List<FeedbackEntity> all = repo.findAll();
    Map<String, Map<String,Integer>> perCatFreq = new HashMap<>();
    for (FeedbackEntity f: all) {
      if (f.getSentimentScore() < -0.1) {
        String c = f.getCategory() == null ? "Others" : f.getCategory();
        perCatFreq.putIfAbsent(c, new HashMap<>());
        String[] tokens = f.getText().toLowerCase().split("\\W+");
        for (String t: tokens) {
          if (t.length() < 3) continue;
          Map<String,Integer> wf = perCatFreq.get(c);
          wf.put(t, wf.getOrDefault(t,0)+1);
        }
      }
    }
    List<String> recs = new ArrayList<>();
    if (perCatFreq.isEmpty()) {
      recs.add("All clear - no urgent negative trends detected.");
    } else {
      for (Map.Entry<String, Map<String,Integer>> e : perCatFreq.entrySet()) {
        String cat = e.getKey();
        Map<String,Integer> wf = e.getValue();
        List<Map.Entry<String,Integer>> sorted = new ArrayList<>(wf.entrySet());
        sorted.sort((a,b)->b.getValue()-a.getValue());
        String top = sorted.size()>0 ? sorted.get(0).getKey() : null;
        if (top != null) {
          if (top.contains("unclear") || top.contains("confusing") || top.contains("difficult")) {
            recs.add("[" + cat + "] Recommendation: Improve clarity in this area - add examples and short videos.");
          } else if (top.contains("slow") || top.contains("boring")) {
            recs.add("[" + cat + "] Recommendation: Increase interactivity - quizzes and polls.");
          } else if (top.contains("problem") || top.contains("issue") || top.contains("bug") || top.contains("delay")) {
            recs.add("[" + cat + "] Recommendation: Investigate technical/process issues and fix root cause.");
          } else {
            recs.add("[" + cat + "] Recommendation: Run a focused mini-survey for '" + top + "'.");
          }
        }
      }
    }
    recs.add("General: Offer weekly quick surveys and publish a brief summary.");
    recs.add("General: Provide office hours and help resources.");
    return recs;
  }
}
