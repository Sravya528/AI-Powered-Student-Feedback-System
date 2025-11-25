package com.ai.feedback.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class FeedbackEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String studentName;

  @Column(length = 2000)
  private String text;

  private String category;

  private String sentimentLabel;
  private double sentimentScore;

  private LocalDateTime createdAt;

  public FeedbackEntity() {}

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getStudentName() { return studentName; }
  public void setStudentName(String studentName) { this.studentName = studentName; }
  public String getText() { return text; }
  public void setText(String text) { this.text = text; }
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  public String getSentimentLabel() { return sentimentLabel; }
  public void setSentimentLabel(String sentimentLabel) { this.sentimentLabel = sentimentLabel; }
  public double getSentimentScore() { return sentimentScore; }
  public void setSentimentScore(double sentimentScore) { this.sentimentScore = sentimentScore; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
