package com.example.polling_application.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CandidateModel {
	
	@Id
    private String modelId;
    private String name;
    private String purpose;
    
    public String getModelId() { 
    	return modelId; 
    	}
    
    public void setModelId(String modelId) { 
    	this.modelId = modelId; 
    	}
    
    public String getName() { 
    	return name; 
    }
    public void setName(String name) { 
    	this.name = name;
    	}
    
    public String getPurpose() {
    	return purpose; 
    	}
  
    public void setPurpose(String purpose) { 
    	this.purpose = purpose; 
    	}
}
