package com.klu.fsd.sdp.init;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.klu.fsd.sdp.model.Answer;
import com.klu.fsd.sdp.model.Option;
import com.klu.fsd.sdp.model.Question;
import com.klu.fsd.sdp.model.Quiz;
import com.klu.fsd.sdp.model.QuizAttempt;
import com.klu.fsd.sdp.model.User;
import com.klu.fsd.sdp.model.User.Role;
import com.klu.fsd.sdp.repository.OptionRepository;
import com.klu.fsd.sdp.repository.QuestionRepository;
import com.klu.fsd.sdp.repository.QuizAttemptRepository;
import com.klu.fsd.sdp.repository.QuizRepository;
import com.klu.fsd.sdp.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private QuizRepository quizRepository;
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Autowired
    private OptionRepository optionRepository;
    
    @Autowired
    private QuizAttemptRepository quizAttemptRepository;
    
    private Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if default data is already initialized
        if (isInitialized()) {
            System.out.println("Data already initialized. Skipping initialization.");
            return;
        }

        try {
            // Create default users
            User admin = createDefaultUsers();
            
            // Create sample quizzes with questions
            List<Quiz> quizzes = createSampleQuizzes();
            
            // Create sample attempts
            createSampleAttempts(quizzes);
            
            System.out.println("Sample data initialized successfully!");
        } catch (Exception e) {
            System.err.println("Error initializing data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean isInitialized() {
        // Check if there are existing quizzes with the specific names we want to create
        return quizRepository.findByTitle("daAV").isPresent() ||
               quizRepository.findByTitle("exam hacthon").isPresent() ||
               quizRepository.findByTitle("ihabjkbaks").isPresent();
    }

    private User createDefaultUsers() {
        // Create or get admin user
        Optional<User> existingAdmin = userRepository.findByEmail("admin@quizapp.com");
        User admin;
        
        if (existingAdmin.isPresent()) {
            admin = existingAdmin.get();
            System.out.println("Admin user already exists: " + admin.getName());
        } else {
            admin = new User(
                    "Admin User",
                    "admin@quizapp.com",
                    "admin123",
                    Role.ADMIN
            );
            userRepository.save(admin);
            System.out.println("Created admin user.");
        }

        // Create or get examiner user
        Optional<User> existingExaminer = userRepository.findByEmail("examiner@quizapp.com");
        User examiner;
        
        if (existingExaminer.isPresent()) {
            examiner = existingExaminer.get();
            System.out.println("Examiner user already exists: " + examiner.getName());
        } else {
            examiner = new User(
                    "Haswanth",
                    "examiner@quizapp.com",
                    "examiner123",
                    Role.EXAMINER
            );
            userRepository.save(examiner);
            System.out.println("Created examiner user.");
        }

        // Create regular users (students) if they don't exist
        String[] studentNames = {
            "Jane Cooper", "Alex Morgan", "Sanjay Patel", "Maria Rodriguez", 
            "John Smith", "Sarah Johnson", "David Chen", "Priya Sharma"
        };
        
        List<User> students = new ArrayList<>();
        for (int i = 0; i < studentNames.length; i++) {
            String name = studentNames[i];
            String email = name.toLowerCase().replace(" ", ".") + "@student.edu";
            
            Optional<User> existingStudent = userRepository.findByEmail(email);
            
            if (existingStudent.isPresent()) {
                students.add(existingStudent.get());
                System.out.println("Student user already exists: " + name);
            } else {
                User student = new User(
                    name,
                    email,
                    "student123",
                    Role.USER
                );
                userRepository.save(student);
                students.add(student);
                System.out.println("Created student user: " + name);
            }
        }
        
        System.out.println("User setup completed successfully!");
        return admin;
    }
    
    private List<Quiz> createSampleQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        User examiner = userRepository.findByEmail("examiner@quizapp.com").orElseThrow();
        
        // First quiz - daAV
        Optional<Quiz> existingDaavQuiz = quizRepository.findByTitle("daAV");
        Quiz daavQuiz;
        
        if (existingDaavQuiz.isPresent()) {
            daavQuiz = existingDaavQuiz.get();
            System.out.println("Quiz 'daAV' already exists");
        } else {
            daavQuiz = new Quiz(
                "daAV",
                "SDVsfs",
                30, // 30 minutes
                examiner
            );
            daavQuiz.setCreatedAt(LocalDateTime.now());
            daavQuiz.setActive(true);
            quizRepository.save(daavQuiz);
            
            // Create one sample question for daAV
            Question daavQuestion = new Question("Sample question for daAV", 10);
            daavQuestion.setQuiz(daavQuiz);
            questionRepository.save(daavQuestion);
            
            // Add options
            Option daavOption1 = new Option("Option 1", true);
            daavOption1.setQuestion(daavQuestion);
            optionRepository.save(daavOption1);
            
            Option daavOption2 = new Option("Option 2", false);
            daavOption2.setQuestion(daavQuestion);
            optionRepository.save(daavOption2);
            
            System.out.println("Created quiz: daAV");
        }
        
        // Second quiz - exam hacthon
        Optional<Quiz> existingHacthonQuiz = quizRepository.findByTitle("exam hacthon");
        Quiz hacthonQuiz;
        
        if (existingHacthonQuiz.isPresent()) {
            hacthonQuiz = existingHacthonQuiz.get();
            System.out.println("Quiz 'exam hacthon' already exists");
        } else {
            hacthonQuiz = new Quiz(
                "exam hacthon",
                "jkabxkhbxk",
                30, // 30 minutes
                examiner
            );
            hacthonQuiz.setCreatedAt(LocalDateTime.now());
            hacthonQuiz.setActive(true);
            quizRepository.save(hacthonQuiz);
            
            // Create one sample question for exam hacthon
            Question hacthonQuestion = new Question("Sample question for hacthon", 10);
            hacthonQuestion.setQuiz(hacthonQuiz);
            questionRepository.save(hacthonQuestion);
            
            // Add options
            Option hacthonOption1 = new Option("Option 1", true);
            hacthonOption1.setQuestion(hacthonQuestion);
            optionRepository.save(hacthonOption1);
            
            Option hacthonOption2 = new Option("Option 2", false);
            hacthonOption2.setQuestion(hacthonQuestion);
            optionRepository.save(hacthonOption2);
            
            System.out.println("Created quiz: exam hacthon");
        }
        
        // Third quiz - ihabjkbaks
        Optional<Quiz> existingIhabjkbaksQuiz = quizRepository.findByTitle("ihabjkbaks");
        Quiz ihabjkbaksQuiz;
        
        if (existingIhabjkbaksQuiz.isPresent()) {
            ihabjkbaksQuiz = existingIhabjkbaksQuiz.get();
            System.out.println("Quiz 'ihabjkbaks' already exists");
        } else {
            ihabjkbaksQuiz = new Quiz(
                "ihabjkbaks",
                "askbxsan",
                112, // 112 minutes
                examiner
            );
            ihabjkbaksQuiz.setCreatedAt(LocalDateTime.now());
            ihabjkbaksQuiz.setActive(true);
            quizRepository.save(ihabjkbaksQuiz);
            
            // Create one sample question for ihabjkbaks
            Question ihabjkbaksQuestion = new Question("Sample question for ihabjkbaks", 10);
            ihabjkbaksQuestion.setQuiz(ihabjkbaksQuiz);
            questionRepository.save(ihabjkbaksQuestion);
            
            // Add options
            Option ihabjkbaksOption1 = new Option("Option 1", true);
            ihabjkbaksOption1.setQuestion(ihabjkbaksQuestion);
            optionRepository.save(ihabjkbaksOption1);
            
            Option ihabjkbaksOption2 = new Option("Option 2", false);
            ihabjkbaksOption2.setQuestion(ihabjkbaksQuestion);
            optionRepository.save(ihabjkbaksOption2);
            
            System.out.println("Created quiz: ihabjkbaks");
        }
        
        // Fourth quiz - Advanced React Concepts
        Optional<Quiz> existingReactQuiz = quizRepository.findByTitle("Advanced React Concepts");
        Quiz reactQuiz;
        
        if (existingReactQuiz.isPresent()) {
            reactQuiz = existingReactQuiz.get();
            System.out.println("Quiz 'Advanced React Concepts' already exists");
        } else {
            reactQuiz = new Quiz(
                "Advanced React Concepts",
                "Deep dive into advanced React concepts including hooks, context, and performance optimization",
                45, // 45 minutes
                examiner
            );
            reactQuiz.setCreatedAt(LocalDateTime.now());
            reactQuiz.setActive(true);
            quizRepository.save(reactQuiz);
            
            // React questions
            createReactQuestions(reactQuiz);
            
            System.out.println("Created quiz: Advanced React Concepts");
        }
        
        // Add quizzes to the list
        quizzes.add(daavQuiz);
        quizzes.add(hacthonQuiz);
        quizzes.add(ihabjkbaksQuiz);
        quizzes.add(reactQuiz);
        
        System.out.println("Quiz setup completed successfully!");
        return quizzes;
    }
    
    private void createReactQuestions(Quiz quiz) {
        // Question 1
        Question q1 = new Question("Which hook is used to perform side effects in a functional component?", 10);
        q1.setQuiz(quiz);
        questionRepository.save(q1);
        
        Option q1o1 = new Option("useState", false);
        q1o1.setQuestion(q1);
        optionRepository.save(q1o1);
        
        Option q1o2 = new Option("useEffect", true); // Correct
        q1o2.setQuestion(q1);
        optionRepository.save(q1o2);
        
        Option q1o3 = new Option("useContext", false);
        q1o3.setQuestion(q1);
        optionRepository.save(q1o3);
        
        Option q1o4 = new Option("useReducer", false);
        q1o4.setQuestion(q1);
        optionRepository.save(q1o4);
        
        // Question 2
        Question q2 = new Question("What is the correct way to conditionally render a component in React?", 10);
        q2.setQuiz(quiz);
        questionRepository.save(q2);
        
        Option q2o1 = new Option("if (condition) { return <Component/>; }", false);
        q2o1.setQuestion(q2);
        optionRepository.save(q2o1);
        
        Option q2o2 = new Option("{condition && <Component/>}", true); // Correct
        q2o2.setQuestion(q2);
        optionRepository.save(q2o2);
        
        Option q2o3 = new Option("<Condition test={condition}><Component/></Condition>", false);
        q2o3.setQuestion(q2);
        optionRepository.save(q2o3);
        
        Option q2o4 = new Option("condition ? <Component/> : null", false);
        q2o4.setQuestion(q2);
        optionRepository.save(q2o4);
        
        // Question 3
        Question q3 = new Question("What is the purpose of React.memo?", 10);
        q3.setQuiz(quiz);
        questionRepository.save(q3);
        
        Option q3o1 = new Option("To memoize a component to prevent unnecessary re-renders", true); // Correct
        q3o1.setQuestion(q3);
        optionRepository.save(q3o1);
        
        Option q3o2 = new Option("To create a memoization cache for component state", false);
        q3o2.setQuestion(q3);
        optionRepository.save(q3o2);
        
        Option q3o3 = new Option("To store values that persist after component unmount", false);
        q3o3.setQuestion(q3);
        optionRepository.save(q3o3);
        
        Option q3o4 = new Option("To memorize event handlers for performance", false);
        q3o4.setQuestion(q3);
        optionRepository.save(q3o4);
        
        // Question 4
        Question q4 = new Question("Which of the following is NOT a React Hook?", 10);
        q4.setQuiz(quiz);
        questionRepository.save(q4);
        
        Option q4o1 = new Option("useEffect", false);
        q4o1.setQuestion(q4);
        optionRepository.save(q4o1);
        
        Option q4o2 = new Option("useState", false);
        q4o2.setQuestion(q4);
        optionRepository.save(q4o2);
        
        Option q4o3 = new Option("useDispatch", false);
        q4o3.setQuestion(q4);
        optionRepository.save(q4o3);
        
        Option q4o4 = new Option("useHistory", true); // Correct - it's from react-router, not React core
        q4o4.setQuestion(q4);
        optionRepository.save(q4o4);
        
        // Question 5
        Question q5 = new Question("What is the purpose of React Context?", 10);
        q5.setQuiz(quiz);
        questionRepository.save(q5);
        
        Option q5o1 = new Option("To manage global HTML attributes", false);
        q5o1.setQuestion(q5);
        optionRepository.save(q5o1);
        
        Option q5o2 = new Option("To provide a way to pass data through the component tree without prop-drilling", true); // Correct
        q5o2.setQuestion(q5);
        optionRepository.save(q5o2);
        
        Option q5o3 = new Option("To create contextual CSS styling", false);
        q5o3.setQuestion(q5);
        optionRepository.save(q5o3);
        
        Option q5o4 = new Option("To add context-sensitive help to components", false);
        q5o4.setQuestion(q5);
        optionRepository.save(q5o4);
    }
    
    @Transactional
    private void createSampleAttempts(List<Quiz> quizzes) {
        // Find students
        List<User> students = userRepository.findByRole(Role.USER);
        if (students.isEmpty()) {
            System.out.println("No students found, skipping attempt creation");
            return;
        }
        
        try {
            // For each quiz, create specific attempts if none exist
            for (Quiz quiz : quizzes) {
                // Check if this quiz already has attempts
                List<QuizAttempt> existingAttempts = quizAttemptRepository.findByQuiz(quiz);
                if (!existingAttempts.isEmpty()) {
                    System.out.println("Quiz attempts already exist for " + quiz.getTitle() + ", skipping attempt creation");
                    continue;
                }
                
                // Create sample attempts based on quiz type
                if (quiz.getTitle().equals("Advanced React Concepts")) {
                    // Jane Cooper attempt - 85% score, 22 minutes
                    User janeCooper = findOrCreateStudent("Jane Cooper", students);
                    if (janeCooper != null) {
                        createSpecificAttempt(quiz, janeCooper, 85, 22, "2023-09-15T14:30:00");
                    }
                    
                    // Alex Morgan attempt - 70% score, 28 minutes
                    User alexMorgan = findOrCreateStudent("Alex Morgan", students);
                    if (alexMorgan != null) {
                        createSpecificAttempt(quiz, alexMorgan, 70, 28, "2023-09-15T10:15:00");
                    }
                } 
                else if (quiz.getTitle().equals("daAV")) {
                    // Jane Cooper attempt - 85% score, 22 minutes
                    User janeCooper = findOrCreateStudent("Jane Cooper", students);
                    if (janeCooper != null) {
                        createSpecificAttempt(quiz, janeCooper, 85, 22, "2023-09-15T14:30:00");
                    }
                    
                    // Alex Morgan attempt - 70% score, 28 minutes
                    User alexMorgan = findOrCreateStudent("Alex Morgan", students);
                    if (alexMorgan != null) {
                        createSpecificAttempt(quiz, alexMorgan, 70, 28, "2023-09-15T10:15:00");
                    }
                    
                    // Add Sanjay Patel with 90% score
                    User sanjayPatel = findOrCreateStudent("Sanjay Patel", students);
                    if (sanjayPatel != null) {
                        createSpecificAttempt(quiz, sanjayPatel, 90, 25, "2023-09-16T09:45:00");
                    }
                }
                else if (quiz.getTitle().equals("exam hacthon")) {
                    // Maria Rodriguez attempt - 82% score
                    User mariaRodriguez = findOrCreateStudent("Maria Rodriguez", students);
                    if (mariaRodriguez != null) {
                        createSpecificAttempt(quiz, mariaRodriguez, 82, 26, "2023-09-17T11:20:00");
                    }
                    
                    // John Smith attempt - 75% score
                    User johnSmith = findOrCreateStudent("John Smith", students);
                    if (johnSmith != null) {
                        createSpecificAttempt(quiz, johnSmith, 75, 24, "2023-09-17T14:15:00");
                    }
                }
                else if (quiz.getTitle().equals("ihabjkbaks")) {
                    // Sarah Johnson attempt - 88% score
                    User sarahJohnson = findOrCreateStudent("Sarah Johnson", students);
                    if (sarahJohnson != null) {
                        createSpecificAttempt(quiz, sarahJohnson, 88, 95, "2023-09-18T10:30:00");
                    }
                    
                    // David Chen attempt - 92% score
                    User davidChen = findOrCreateStudent("David Chen", students);
                    if (davidChen != null) {
                        createSpecificAttempt(quiz, davidChen, 92, 100, "2023-09-18T15:45:00");
                    }
                }
            }
            
            System.out.println("Sample quiz attempts created successfully!");
        } catch (Exception e) {
            System.err.println("Error creating sample attempts: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private User findOrCreateStudent(String name, List<User> existingStudents) {
        // First try to find in the existing list
        for (User student : existingStudents) {
            if (student.getName().equals(name)) {
                return student;
            }
        }
        
        // If not found, try to get from repository
        String email = name.toLowerCase().replace(" ", ".") + "@student.edu";
        Optional<User> existingStudent = userRepository.findByEmail(email);
        if (existingStudent.isPresent()) {
            return existingStudent.get();
        }
        
        // If still not found, create a new student
        User newStudent = new User(
            name,
            email,
            "student123",
            Role.USER
        );
        try {
            userRepository.save(newStudent);
            System.out.println("Created new student: " + name);
            return newStudent;
        } catch (Exception e) {
            System.err.println("Error creating student " + name + ": " + e.getMessage());
            return null;
        }
    }
    
    private void createSpecificAttempt(Quiz quiz, User student, int score, int minutesTaken, String completionTimeStr) {
        QuizAttempt attempt = new QuizAttempt(student, quiz);
        
        // Parse completion time
        LocalDateTime completedAt = LocalDateTime.parse(completionTimeStr);
        LocalDateTime startedAt = completedAt.minusMinutes(minutesTaken);
        
        attempt.setStartedAt(startedAt);
        attempt.setCompletedAt(completedAt);
        attempt.setCompleted(true);
        attempt.setScore(score);
        
        quizAttemptRepository.save(attempt);
        
        // Add answers for each question with a distribution that matches the score
        List<Question> questions = quiz.getQuestions();
        int totalQuestions = questions.size();
        if (totalQuestions == 0) {
            System.out.println("No questions found for quiz: " + quiz.getTitle());
            return;
        }
        
        int correctAnswersNeeded = (int) Math.round((score / 100.0) * totalQuestions);
        
        int questionIndex = 0;
        for (Question question : questions) {
            // Determine if this answer should be correct based on needed score
            boolean shouldBeCorrect = questionIndex < correctAnswersNeeded;
            
            // Get the correct or incorrect option based on shouldBeCorrect
            List<Option> options = question.getOptions();
            if (options.isEmpty()) {
                System.out.println("No options found for question ID: " + question.getId());
                continue;
            }
            
            Option selectedOption;
            
            if (shouldBeCorrect) {
                selectedOption = options.stream()
                    .filter(Option::isCorrect)
                    .findFirst()
                    .orElse(options.get(0));
            } else {
                selectedOption = options.stream()
                    .filter(o -> !o.isCorrect())
                    .findFirst()
                    .orElse(options.get(0));
            }
            
            // Create and save answer
            Answer answer = new Answer(attempt, question, selectedOption);
            attempt.addAnswer(answer);
            
            questionIndex++;
        }
        
        // Update attempt with answers
        quizAttemptRepository.save(attempt);
    }
} 