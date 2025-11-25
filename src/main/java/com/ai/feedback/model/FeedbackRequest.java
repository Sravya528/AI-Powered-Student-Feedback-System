package com.ai.feedback.model;

public class FeedbackRequest {
  private String studentName;
  private String text;
  private String category;

  public FeedbackRequest() {}
  public String getStudentName() { return studentName; }
  public void setStudentName(String studentName) { this.studentName = studentName; }
  public String getText() { return text; }
  public void setText(String text) { this.text = text; }
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
}
