package com.example.polling_application.repository;

import com.example.polling_application.model.CandidateModel;
import com.example.polling_application.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateModelRepository extends JpaRepository<CandidateModel, String> {
	public interface VoteRepository extends JpaRepository<Vote, String> {}
}
