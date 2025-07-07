package com.example.polling_application.repository;

import com.example.polling_application.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, String> {
    List<Vote> findByVoterId(String voterId);
    List<Vote> findByModelId(String modelId);
    boolean existsByVoterId(String voterId);
}
