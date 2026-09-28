package com.example.demo.service;

import java.util.List;

import com.example.demo.model.OcFeedback;
import com.example.demo.payload.OcFeedbackRequest;

public interface OcFeedbackService {
    OcFeedback submit(OcFeedbackRequest request, String username);
    List<OcFeedback> getAll();
    byte[] exportExcel();
}
