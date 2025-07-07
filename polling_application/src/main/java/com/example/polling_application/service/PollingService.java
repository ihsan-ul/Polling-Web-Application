package com.example.polling_application.service;

import com.example.polling_application.model.*;
import com.example.polling_application.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PollingService {

    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private CandidateModelRepository modelRepository;
    @Autowired
    private RestTemplate restTemplate;
    

    private boolean pollOpen = true;

    public Vote castVote(Vote vote) {
        if (!pollOpen)
            throw new IllegalStateException("Poll is closed");
        if (modelRepository.count() == 0)
            throw new IllegalStateException("No models available for voting.");
        if (!modelRepository.existsById(vote.getModelId()))
            throw new IllegalArgumentException("Model with ID " + vote.getModelId() + " does not exist.");

        Optional<Vote> existingVote = voteRepository.findByVoterId(vote.getVoterId())
                .stream()
                .findFirst();

        if (existingVote.isPresent()) {
            Vote existing = existingVote.get();

            boolean sameName = Objects.equals(existing.getVoterName(), vote.getVoterName());
            boolean sameInterests = new java.util.HashSet<>(existing.getInterests())
                    .equals(new java.util.HashSet<>(vote.getInterests()));
            boolean sameModelId = Objects.equals(existing.getModelId(), vote.getModelId());

            if (sameName && sameInterests && sameModelId) {
                throw new IllegalArgumentException("You have already cast this vote.");
            }

            voteRepository.delete(existing);
        }

        return voteRepository.save(vote);
    }

    public Optional<Vote> getVote(String voteId) {
        return voteRepository.findById(voteId);
    }

    public List<CandidateModel> getAllModels() {
        return modelRepository.findAll();
    }

    public Map<String, Map<String, Object>> tallyVotes() {
        if (pollOpen) {
            throw new IllegalStateException("Tallying is only allowed when the poll is closed.");
        }
        Map<String, Map<String, Object>> results = new LinkedHashMap<>();
        modelRepository.findAll().forEach(model -> {
            List<Vote> votes = voteRepository.findByModelId(model.getModelId());
            String mostCommonInterest = votes.stream()
                    .flatMap(v -> v.getInterests().stream())
                    .filter(s -> s != null && !s.trim().isEmpty())
                    .collect(Collectors.groupingBy(String::toString, Collectors.counting()))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("No interests detected");
            results.put(model.getModelId(), Map.of(
                "modelName", model.getName(),
                "totalVotes", votes.size(),
                "mostCommonInterest", mostCommonInterest
            ));
        });
        return results;
    }



    public void validateVoters() {
        voteRepository.findAll().forEach(vote -> {
            try {
                Map<String, Object> profile = getVoterProfile(vote.getVoterId());
                String officialName = (String) profile.get("name");
                List<String> officialInterests = (List<String>) profile.get("interests");
                List<String> voteInterests = vote.getInterests();

                List<String> normVoteInterests = normalize(voteInterests);
                List<String> normOfficialInterests = normalize(officialInterests);

                boolean nameMatches = Objects.equals(vote.getVoterName(), officialName);
                boolean interestsMatch = new HashSet<>(normVoteInterests).equals(new HashSet<>(normOfficialInterests));

                

                if (!nameMatches || !interestsMatch) {
                    voteRepository.delete(vote);
                }
            } catch (Exception e) {
            	if (e.getMessage().contains("doesn't exist in the voter profile service database")) {
                    voteRepository.delete(vote);
                } else {
                	throw new IllegalStateException("Error validating all the voters.");
                }
            }
        });
    }

    
    private List<String> normalize(List<String> list) {
        if (list == null) return Collections.emptyList();
        return list.stream()
            .filter(s -> s != null && !s.trim().isEmpty())
            .collect(Collectors.toList());
    }

    



    public CandidateModel addModel(CandidateModel model) {
        if (modelRepository.existsById(model.getModelId())) {
            throw new IllegalArgumentException("Model ID already exists");
        }
        return modelRepository.save(model);
    }

    public void removeModel(String modelId) {
        if (!modelRepository.existsById(modelId)) {
            throw new IllegalArgumentException("Model with ID " + modelId + " does not exist.");
        }
        List<Vote> votesToDelete = voteRepository.findByModelId(modelId);
        voteRepository.deleteAll(votesToDelete);
        modelRepository.deleteById(modelId);
    }


    public void setPollStatus(boolean open) {
        this.pollOpen = open;
    }

    private Map<String, Object> getVoterProfile(String voterId) {
        String url = "https://pmaier.eu.pythonanywhere.com/vps/voter/" + voterId;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);         
            Map<String, Object> voter = (Map<String, Object>) response.getBody().get("voter");
            return Map.of("name", voter.get("name"), "interests", voter.get("interests"));
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new IllegalArgumentException("The entered voter ID doesn't exist in the voter profile service database.");
            } else {
                throw new IllegalArgumentException("Error contacting voter profile service: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error contacting voter profile service: " + e.getMessage());
        }
    }

    private boolean voterExists(String voterId) {
    	String url = "https://pmaier.eu.pythonanywhere.com/vps/voter/" + voterId;
        try {
            return restTemplate.getForEntity(url, Object.class).getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            return false;
        }
    }

    
    public boolean retractVote(String voterId) {
        if (!pollOpen) {
            throw new IllegalStateException("The poll is closed. You cannot retract your vote.");
        }

        List<Vote> vote = voteRepository.findByVoterId(voterId)
                                               .stream()
                                               .collect(Collectors.toList());

        if (vote.isEmpty()) {
            return false;
        }

        voteRepository.deleteAll(vote);
        return true;
    }


    
    public Optional<Vote> getByVoterId(String voterId) {
        return voteRepository.findByVoterId(voterId).stream()
            .findFirst();
    }

	public boolean getPollStatus() {
		return this.pollOpen;
	}
}