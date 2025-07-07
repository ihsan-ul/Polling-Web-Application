package com.example.polling_application.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Vote {
 
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
    private String voteId;
    private String voterId;
    private String modelId;
    
    @ElementCollection
    private List<String> interests;
    private String voterName;

    public String getVoteId() { return voteId; }
    public void setVoteId(String voteId) { this.voteId = voteId; }
    public String getVoterId() { return voterId; }
    public void setVoterId(String voterId) { this.voterId = voterId; }
    public String getModelId() { return modelId; }
    public void setModelId(String modelId) { this.modelId = modelId; }
  
    public List<String> getInterests() { return interests; }
    public void setInterests(List<String> interests) { this.interests = interests; }
    public String getVoterName() { return voterName; }
    public void setVoterName(String voterName) { this.voterName = voterName; }
}
