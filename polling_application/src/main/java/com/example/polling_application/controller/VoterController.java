package com.example.polling_application.controller;

import com.example.polling_application.model.CandidateModel;
import com.example.polling_application.model.Vote;
import com.example.polling_application.service.PollingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/votes")
public class VoterController {

    @Autowired
    private PollingService pollingService;

    @PostMapping
    public ResponseEntity<?> castVote(@RequestBody Vote vote) {
        try {
            Vote createdVote = pollingService.castVote(vote);
            return ResponseEntity.created(
                linkTo(methodOn(VoterController.class).getVote(createdVote.getVoteId())).toUri())
                .body(createdVote);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{voteId}")
    public ResponseEntity<Vote> getVote(@PathVariable String voteId) {
        Optional<Vote> vote = pollingService.getVote(voteId);
        return vote.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{voterId}")
    public ResponseEntity<String> retractVote(@PathVariable String voterId) {
        try {
            boolean retracted = pollingService.retractVote(voterId);
            if (retracted) {
                return ResponseEntity.ok("Vote retracted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No vote found for this voter.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/model/{voterId}")
    public ResponseEntity<?> getVotedModel(@PathVariable String voterId) {
        Optional<Vote> vote = pollingService.getByVoterId(voterId);

        if (vote.isPresent()) {
            return ResponseEntity.ok(vote.get().getModelId());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No active vote found for this voter.");
        }
    }

    @GetMapping("/models")
    public List<CandidateModel> getAllModels() {
        return pollingService.getAllModels();
    }
}