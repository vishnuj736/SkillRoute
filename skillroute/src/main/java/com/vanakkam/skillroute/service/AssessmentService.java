package com.vanakkam.skillroute.service;



import com.vanakkam.skillroute.dto.AssessmentRequest;
import com.vanakkam.skillroute.dto.QuestionRequest;
import com.vanakkam.skillroute.model.Assessment;
import com.vanakkam.skillroute.model.Course;
import com.vanakkam.skillroute.model.Question;
import com.vanakkam.skillroute.repository.AssessmentRepository;
import com.vanakkam.skillroute.repository.CourseRepository;
import com.vanakkam.skillroute.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Assessment createAssessment(Long courseId, AssessmentRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Assessment assessment = Assessment.builder()
                .title(request.getTitle())
                .course(course)
                .build();

        return assessmentRepository.save(assessment);
    }

    @Transactional
    public Question addQuestion(Long assessmentId, QuestionRequest request) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        Question question = Question.builder()
                .questionText(request.getQuestionText())
                .skillTag(request.getSkillTag())
                .assessment(assessment)
                .build();

        return questionRepository.save(question);
    }
}