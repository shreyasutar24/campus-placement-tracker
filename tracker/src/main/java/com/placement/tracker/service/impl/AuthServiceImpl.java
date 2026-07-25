package com.placement.tracker.service.impl;

import com.placement.tracker.dto.AuthResponse;
import com.placement.tracker.dto.LoginRequest;
import com.placement.tracker.dto.RegisterRequest;
import com.placement.tracker.entity.*;
import com.placement.tracker.repository.*;
import com.placement.tracker.security.JwtUtil;
import com.placement.tracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentRecordRepository studentRecordRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("${admin.email}")
    private String designatedAdminEmail;

    @Value("${admin.registration.code}")
    private String adminRegistrationCode;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        User user = userRepository.findByEmail(request.getEmail()).get();

        if (user.getRole() == Role.ROLE_ADMIN) {
            boolean isDesignatedAdmin = designatedAdminEmail.equalsIgnoreCase(user.getEmail());
            boolean codeMatches = request.getAdminCode() != null
                    && adminRegistrationCode.equals(request.getAdminCode());

            if (!isDesignatedAdmin || !codeMatches) {
                throw new RuntimeException("Invalid admin code.");
            }
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole().name());
        response.setUserId(user.getId());

        return response;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered.");
        }

        Role role;

     
     // -------- Admin Registration --------
        if (request.getAdminCode() != null && !request.getAdminCode().trim().isEmpty()) {

            if ("Placement Officer".equalsIgnoreCase(request.getFullName())
                    && "admin@college.com".equalsIgnoreCase(request.getEmail())
                    && "ADMIN123".equals(request.getAdminCode())) {

                role = Role.ROLE_ADMIN;

            } else {
                throw new RuntimeException("Invalid Admin Credentials.");
            }

        } else {

            try {
                role = Role.valueOf(request.getRole());
            } catch (Exception e) {
                throw new RuntimeException("Invalid role: " + request.getRole());
            }

        }
        if (role == Role.ROLE_STUDENT) {

            StudentRecord record = studentRecordRepository
                    .findByRollNumberAndUniversityNumber(
                            request.getRollNumber(), request.getUniversityNumber())
                    .orElseThrow(() -> new RuntimeException(
                            "Invalid student details. Please contact placement office."));

            if (record.isRegistered()) {
                throw new RuntimeException("Student already registered with this roll number.");
            }

            User user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(role);
            userRepository.save(user);

            // FIX: Student entity me sirf yeh fields hain (rollNumber/universityNumber/passingYear StudentRecord me hain)
            Student student = new Student();
            student.setUser(user);
            student.setDepartment(record.getDepartment());
            student.setStudentRecord(record);
            studentRepository.save(student);

            record.setRegistered(true);
            studentRecordRepository.save(record);

            // FIX: generateToken needs email + role (String)
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setRole(user.getRole().name());
            response.setUserId(user.getId());
            return response;
        }

        if (role == Role.ROLE_TEACHER) {

            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));

            User user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(role);
            userRepository.save(user);

            Teacher teacher = new Teacher();
            teacher.setUser(user);
            teacher.setDepartment(department);
            teacherRepository.save(teacher);

            // FIX: generateToken needs email + role (String)
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setRole(user.getRole().name());
            response.setUserId(user.getId());
            return response;
        }

        if (role == Role.ROLE_ADMIN) {

            User user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(role);
            userRepository.save(user);

            // FIX: generateToken needs email + role (String)
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setEmail(user.getEmail());
            response.setFullName(user.getFullName());
            response.setRole(user.getRole().name());
            response.setUserId(user.getId());
            return response;
        }

        throw new RuntimeException("Invalid registration request.");
    }
}