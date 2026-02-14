package com.example.servicetest.service;

import com.example.servicetest.entity.AffectationTest;
import com.example.servicetest.entity.QuestionTest;
import com.example.servicetest.repository.AffectationTestRepository;
import com.example.servicetest.repository.QuestionTestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AffectationTestService {

    private final AffectationTestRepository affectationTestRepository;
    private final QuestionTestRepository questionTestRepository;

    public AffectationTestService(AffectationTestRepository affectationTestRepository,
                                  QuestionTestRepository questionTestRepository) {
        this.affectationTestRepository = affectationTestRepository;
        this.questionTestRepository = questionTestRepository;
    }

    // ✅ CREATE
    public AffectationTest createAffectation(AffectationTest affectation) {
        return affectationTestRepository.save(affectation);
    }

    // ✅ READ ALL
    public List<AffectationTest> getAllAffectations() {
        return affectationTestRepository.findAll();
    }

    // ✅ READ BY ID
    public AffectationTest getAffectationById(Long id) {
        return affectationTestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affectation not found with id: " + id));
    }

    // ✅ UPDATE
    public AffectationTest updateAffectation(Long id, AffectationTest updated) {

        AffectationTest existing = getAffectationById(id);

        existing.setFreelancerId(updated.getFreelancerId());
        existing.setTestDate(updated.getTestDate());
        existing.setScore(updated.getScore());
        existing.setIsValidated(updated.getIsValidated());
        existing.setCorrectAnswersCount(updated.getCorrectAnswersCount());
        existing.setTotalQuestions(updated.getTotalQuestions());
        existing.setTimeSpent(updated.getTimeSpent());
        existing.setStatus(updated.getStatus());

        return affectationTestRepository.save(existing);
    }

    // ✅ DELETE
    public void deleteAffectation(Long id) {
        AffectationTest affectation = getAffectationById(id);
        affectationTestRepository.delete(affectation);
    }

    // ✅ Ajouter une question à une affectation
    public AffectationTest addQuestionToAffectation(Long affectationId, Long questionId) {

        AffectationTest affectation = getAffectationById(affectationId);

        QuestionTest question = questionTestRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));

        affectation.addQuestion(question);

        return affectationTestRepository.save(affectation);
    }

    // ✅ Supprimer une question d’une affectation
    public AffectationTest removeQuestionFromAffectation(Long affectationId, Long questionId) {

        AffectationTest affectation = getAffectationById(affectationId);

        QuestionTest question = questionTestRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + questionId));

        affectation.removeQuestion(question);

        return affectationTestRepository.save(affectation);
    }









}
