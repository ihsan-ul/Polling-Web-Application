package com.example.polling_application.controller;

import com.example.polling_application.model.CandidateModel;
import com.example.polling_application.service.PollingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired private PollingService pollingService;
    
    @GetMapping("/tally")
    public ResponseEntity<?> getVoteTally() {
        try {
            return ResponseEntity.ok(pollingService.tallyVotes());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    
    @PostMapping("/validate")
    public ResponseEntity<String> validateVoters() {
    	try {
        pollingService.validateVoters();
        return ResponseEntity.ok("Voter validation completed");
    	} catch(IllegalStateException e) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    	}
    }
    
    @PostMapping("/model")
    public ResponseEntity<?> addModel(@RequestBody CandidateModel model) {
        try {
            CandidateModel created = pollingService.addModel(model);
            return ResponseEntity.ok(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    
    @DeleteMapping("/model/{modelId}")
    public ResponseEntity<?> removeModel(@PathVariable String modelId) {
        try {
            pollingService.removeModel(modelId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    
    @PutMapping("/poll-status")
    public ResponseEntity<String> setPollStatus(@RequestParam boolean open) {
        pollingService.setPollStatus(open);
        return ResponseEntity.ok("Poll status updated");
    }
    
    @GetMapping("/poll-status")
    public ResponseEntity<Boolean> getPollStatus() {
        boolean isOpen = pollingService.getPollStatus(); 
        return ResponseEntity.ok(isOpen);
    }

    
    @GetMapping("/models")
    public ResponseEntity<List<CandidateModel>> getAllModels() {
        List<CandidateModel> models = pollingService.getAllModels();
        return ResponseEntity.ok(models);
    }

}
