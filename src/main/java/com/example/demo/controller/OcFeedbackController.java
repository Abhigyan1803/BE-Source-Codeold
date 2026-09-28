package com.example.demo.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.JwtTokenUtil;
import com.example.demo.model.OcFeedback;
import com.example.demo.payload.OcFeedbackRequest;
import com.example.demo.service.OcFeedbackService;
import com.example.demo.util.ResponseMessage;

@RestController
@RequestMapping("/api/oc-feedback")
@CrossOrigin
public class OcFeedbackController {

    @Autowired private OcFeedbackService feedbackService;
    @Autowired private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/submit")
    @PreAuthorize("hasRole('CADET')")
    public ResponseEntity<?> submit(@RequestBody OcFeedbackRequest request, HttpServletRequest servletRequest) {
        String username = jwtTokenUtil.getUsernameFromToken(
                servletRequest.getHeader("Authorization").replace("Bearer ", ""));
        OcFeedback response = feedbackService.submit(request, username);
        return new ResponseEntity<>(
                new ResponseMessage("Feedback submitted successfully.", HttpStatus.OK, response),
                HttpStatus.OK);
    }

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated() and !hasRole('CADET') and !hasRole('USER')")
    public ResponseEntity<?> list() {
        List<OcFeedback> response = feedbackService.getAll();
        return new ResponseEntity<>(
                new ResponseMessage("Records found successfully.", HttpStatus.OK, response),
                HttpStatus.OK);
    }

    @GetMapping("/export")
    @PreAuthorize("isAuthenticated() and !hasRole('CADET') and !hasRole('USER')")
    public ResponseEntity<byte[]> export() {
        byte[] file = feedbackService.exportExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "OC_Feedback.xlsx");
        return new ResponseEntity<>(file, headers, HttpStatus.OK);
    }
}
