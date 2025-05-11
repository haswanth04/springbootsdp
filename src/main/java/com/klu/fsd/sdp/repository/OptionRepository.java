package com.klu.fsd.sdp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.klu.fsd.sdp.model.Option;
import com.klu.fsd.sdp.model.Question;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByQuestion(Question question);
}
