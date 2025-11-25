package com.ai.feedback.service;

import java.util.Set;
import java.util.HashSet;

public class SentimentAnalyzer {

  private final Set<String> positive = new HashSet<>();
  private final Set<String> negative = new HashSet<>();

  public SentimentAnalyzer() {
    String[] pos = {"good","great","excellent","awesome","love","liked","satisfied","happy","useful","helpful","clear","easy","fantastic","amazing","best","informative","engaging"};
    String[] neg = {"bad","poor","hate","worst","difficult","hard","confusing","unsatisfied","boring","slow","problem","issue","dislike","terrible","awful","unclear","delay"};
    for (String w: pos) positive.add(w);
    for (String w: neg) negative.add(w);
  }

  public double score(String text) {
    if (text == null || text.trim().isEmpty()) return 0.0;
    String[] tokens = text.toLowerCase().split("[\\s,\\.\\!\\?;:\\-()\\[\\]'\\\"]+");
    int posc = 0, negc = 0, total = 0;
    for (String t : tokens) {
      if (t == null) continue;
      t = t.trim();
      if (t.isEmpty()) continue;
      total++;
      if (positive.contains(t)) posc++;
      if (negative.contains(t)) negc++;
    }
    if (total == 0) return 0.0;
    return (double)(posc - negc) / total;
  }

  public String label(double score) {
    if (score > 0.15) return "Positive";
    if (score < -0.15) return "Negative";
    return "Neutral";
  }
}
