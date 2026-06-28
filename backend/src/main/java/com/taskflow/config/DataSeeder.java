package com.taskflow.config;

import com.taskflow.entity.*;
import com.taskflow.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, ProjectRepository projectRepository,
                      TaskRepository taskRepository, CommentRepository commentRepository,
                      NotificationRepository notificationRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Always ensure the seed users exist — upsert by email
        seedUsers();

        // Only seed projects/tasks once
        if (projectRepository.count() == 0) {
            seedProjects();
        }
    }

    private void seedUsers() {
        // ── Admins ─────────────────────────────────────────────────
        upsert("Tamizhazhagan", "tamizhazhagan@taskflowpro.com",
                "Admin@123", Role.ADMIN, "#6366f1", "Project Management");
        upsert("Natarajan", "natarajan@taskflowpro.com",
                "Admin@123", Role.ADMIN, "#7c3aed", "Project Management");

        // ── Managers ───────────────────────────────────────────────
        upsert("Rajasekar", "rajasekar@taskflowpro.com",
                "Manager@123", Role.MANAGER, "#2563eb", "Software Development");
        upsert("Balaji", "balaji@taskflowpro.com",
                "Manager@123", Role.MANAGER, "#0891b2", "Software Development");
        upsert("Gopinath", "gopinath@taskflowpro.com",
                "Manager@123", Role.MANAGER, "#0d9488", "Software Development");

        // ── Team Leads ─────────────────────────────────────────────
        upsert("Vignesh", "vignesh@taskflowpro.com",
                "Lead@123", Role.TEAM_LEAD, "#d97706", "Software Development");
        upsert("Suresh Kumar", "suresh.kumar@taskflowpro.com",
                "Lead@123", Role.TEAM_LEAD, "#b45309", "Software Development");
        upsert("Murugan", "murugan@taskflowpro.com",
                "Lead@123", Role.TEAM_LEAD, "#92400e", "Software Development");
        upsert("Prakash", "prakash@taskflowpro.com",
                "Lead@123", Role.TEAM_LEAD, "#78350f", "UI/UX");

        // ── Developers ─────────────────────────────────────────────
        upsert("Arul Kumar", "arul.kumar@taskflowpro.com",
                "Dev@123", Role.DEVELOPER, "#10b981", "Software Development");
        upsert("Karthikeyan", "karthikeyan@taskflowpro.com",
                "Dev@123", Role.DEVELOPER, "#059669", "Software Development");
        upsert("Praveen Kumar", "praveen.kumar@taskflowpro.com",
                "Dev@123", Role.DEVELOPER, "#047857", "Software Development");
        upsert("Naveen Raj", "naveen.raj@taskflowpro.com",
                "Dev@123", Role.DEVELOPER, "#ef4444", "Quality Assurance");
        upsert("Senthil Kumar", "senthil.kumar@taskflowpro.com",
                "Dev@123", Role.DEVELOPER, "#dc2626", "Software Development");
        upsert("Bharath Raj", "bharath.raj@taskflowpro.com",
                "Dev@123", Role.DEVELOPER, "#b91c1c", "DevOps");
        upsert("Dinesh Kumar", "dinesh.kumar@taskflowpro.com",
                "Dev@123", Role.DEVELOPER, "#8b5cf6", "Software Development");
        upsert("Saravanan", "saravanan@taskflowpro.com",
                "Dev@123", Role.DEVELOPER, "#7c3aed", "Quality Assurance");
    }

    /**
     * Insert if not exists; update role/dept/active if the user exists
     * but may have been created with the old schema (missing department/active).
     */
    private User upsert(String name, String email, String rawPassword,
                        Role role, String color, String department) {
        return userRepository.findByEmail(email).map(existing -> {
            // Fix any rows created before department/active columns existed
            if (existing.getDepartment() == null) existing.setDepartment(department);
            if (!existing.isActive()) existing.setActive(true);
            if (existing.getRole() != role) existing.setRole(role);
            return userRepository.save(existing);
        }).orElseGet(() -> {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setRole(role);
            user.setAvatarColor(color);
            user.setDepartment(department);
            user.setActive(true);
            return userRepository.save(user);
        });
    }

    private void seedProjects() {
        User rajasekar  = userRepository.findByEmail("rajasekar@taskflowpro.com").orElseThrow();
        User balaji     = userRepository.findByEmail("balaji@taskflowpro.com").orElseThrow();
        User gopinath   = userRepository.findByEmail("gopinath@taskflowpro.com").orElseThrow();
        User vignesh    = userRepository.findByEmail("vignesh@taskflowpro.com").orElseThrow();
        User sureshKumar = userRepository.findByEmail("suresh.kumar@taskflowpro.com").orElseThrow();
        User murugan    = userRepository.findByEmail("murugan@taskflowpro.com").orElseThrow();
        User prakash    = userRepository.findByEmail("prakash@taskflowpro.com").orElseThrow();
        User arulKumar  = userRepository.findByEmail("arul.kumar@taskflowpro.com").orElseThrow();
        User karthikeyan = userRepository.findByEmail("karthikeyan@taskflowpro.com").orElseThrow();
        User praveenKumar = userRepository.findByEmail("praveen.kumar@taskflowpro.com").orElseThrow();
        User naveenRaj  = userRepository.findByEmail("naveen.raj@taskflowpro.com").orElseThrow();
        User senthilKumar = userRepository.findByEmail("senthil.kumar@taskflowpro.com").orElseThrow();
        User bharathRaj = userRepository.findByEmail("bharath.raj@taskflowpro.com").orElseThrow();
        User dineshKumar = userRepository.findByEmail("dinesh.kumar@taskflowpro.com").orElseThrow();
        User saravanan  = userRepository.findByEmail("saravanan@taskflowpro.com").orElseThrow();

        // Team Alpha — Manager: Rajasekar, Lead: Vignesh
        Project p1 = createProject("Employee Management System",
                "Comprehensive HR system for managing employee records, payroll, and performance reviews",
                "#6366f1", rajasekar,
                Set.of(rajasekar, vignesh, arulKumar, karthikeyan, praveenKumar));

        // Team Beta — Manager: Balaji, Lead: Murugan
        Project p2 = createProject("E-Commerce Platform",
                "Full-featured e-commerce solution with cart, payments, and order management",
                "#10b981", balaji,
                Set.of(balaji, murugan, naveenRaj, senthilKumar, bharathRaj));

        // Team Gamma — Manager: Gopinath, Lead: Suresh Kumar
        Project p3 = createProject("Banking Portal",
                "Secure internet banking portal with transaction history and fund transfers",
                "#f59e0b", gopinath,
                Set.of(gopinath, sureshKumar, dineshKumar, saravanan));

        Project p4 = createProject("Hospital Management System",
                "Patient records, appointments, billing and doctor scheduling system",
                "#ef4444", rajasekar,
                Set.of(rajasekar, prakash, arulKumar, karthikeyan));

        Project p5 = createProject("Inventory Management System",
                "Real-time inventory tracking with barcode scanning and auto-reorder",
                "#8b5cf6", balaji,
                Set.of(balaji, vignesh, praveenKumar, naveenRaj));

        Project p6 = createProject("Smart Attendance System",
                "Biometric and QR-based attendance with analytics and leave management",
                "#0891b2", gopinath,
                Set.of(gopinath, murugan, senthilKumar, bharathRaj, saravanan));

        // ── Tasks for Employee Management System ───────────────────
        createTask("Design database schema", "ERD for employees, departments, payroll tables",
                TaskStatus.DONE, Priority.HIGH, LocalDate.now().minusDays(10), 0, p1, arulKumar, vignesh);
        createTask("Implement Employee CRUD", "REST APIs for employee create, read, update, delete",
                TaskStatus.DONE, Priority.HIGH, LocalDate.now().minusDays(5), 1, p1, karthikeyan, vignesh);
        createTask("Payroll calculation module", "Gross, deductions, net salary with tax brackets",
                TaskStatus.IN_PROGRESS, Priority.CRITICAL, LocalDate.now().plusDays(3), 2, p1, praveenKumar, vignesh);
        createTask("Leave management workflow", "Apply, approve, reject leave with balance tracking",
                TaskStatus.IN_PROGRESS, Priority.HIGH, LocalDate.now().plusDays(5), 3, p1, arulKumar, vignesh);
        createTask("Performance review module", "360-degree review system with rating and feedback",
                TaskStatus.TODO, Priority.MEDIUM, LocalDate.now().plusDays(14), 4, p1, karthikeyan, vignesh);
        createTask("Department hierarchy management", "Org chart with nested department structure",
                TaskStatus.CODE_REVIEW, Priority.MEDIUM, LocalDate.now().plusDays(2), 5, p1, praveenKumar, vignesh);
        createTask("Employee dashboard UI", "Responsive dashboard with KPIs and recent activity",
                TaskStatus.TESTING, Priority.HIGH, LocalDate.now().plusDays(1), 6, p1, arulKumar, vignesh);

        // ── Tasks for E-Commerce Platform ──────────────────────────
        createTask("Product catalog module", "CRUD for products, categories, variants, and images",
                TaskStatus.DONE, Priority.HIGH, LocalDate.now().minusDays(8), 0, p2, naveenRaj, murugan);
        createTask("Shopping cart service", "Add to cart, update quantity, remove items",
                TaskStatus.DONE, Priority.CRITICAL, LocalDate.now().minusDays(3), 1, p2, senthilKumar, murugan);
        createTask("Payment gateway integration", "Razorpay integration with webhook handling",
                TaskStatus.IN_PROGRESS, Priority.CRITICAL, LocalDate.now().plusDays(2), 2, p2, bharathRaj, murugan);
        createTask("Order management system", "Order lifecycle: placed, confirmed, shipped, delivered",
                TaskStatus.IN_PROGRESS, Priority.HIGH, LocalDate.now().plusDays(4), 3, p2, naveenRaj, murugan);
        createTask("Search and filter", "Faceted search with category and price filters",
                TaskStatus.TODO, Priority.MEDIUM, LocalDate.now().plusDays(10), 4, p2, senthilKumar, murugan);
        createTask("Review and rating system", "Product reviews with image upload and moderation",
                TaskStatus.CODE_REVIEW, Priority.LOW, LocalDate.now().plusDays(7), 5, p2, bharathRaj, murugan);

        // ── Tasks for Banking Portal ───────────────────────────────
        createTask("Security audit", "Penetration testing, OWASP top 10 fixes",
                TaskStatus.IN_PROGRESS, Priority.CRITICAL, LocalDate.now().minusDays(1), 0, p3, dineshKumar, sureshKumar);
        createTask("Account dashboard", "Balance overview, recent transactions, quick actions",
                TaskStatus.DONE, Priority.HIGH, LocalDate.now().minusDays(6), 1, p3, saravanan, sureshKumar);
        createTask("Fund transfer module", "NEFT, RTGS, IMPS with OTP verification",
                TaskStatus.IN_PROGRESS, Priority.CRITICAL, LocalDate.now().plusDays(1), 2, p3, dineshKumar, sureshKumar);
        createTask("Statement download", "PDF and Excel export with date range filter",
                TaskStatus.TESTING, Priority.MEDIUM, LocalDate.now().plusDays(3), 3, p3, saravanan, sureshKumar);
        createTask("Two-factor authentication", "TOTP and SMS OTP for login and transactions",
                TaskStatus.BLOCKED, Priority.CRITICAL, LocalDate.now().plusDays(5), 4, p3, dineshKumar, sureshKumar);

        // ── Comments ───────────────────────────────────────────────
        Task payrollTask = taskRepository.findByProjectIdOrderByPositionAsc(p1.getId()).get(2);
        saveComment(payrollTask, praveenKumar,
                "Tax bracket logic is complete. Working on PF and ESI deductions. ETA: 2 days.");
        saveComment(payrollTask, vignesh,
                "Good progress Praveen. Make sure we handle arrears for mid-month joiners.");

        Task paymentTask = taskRepository.findByProjectIdOrderByPositionAsc(p2.getId()).get(2);
        saveComment(paymentTask, bharathRaj,
                "Razorpay sandbox integrated. Webhook tested for success and failure cases.");

        // ── Notifications ──────────────────────────────────────────
        saveNotification(praveenKumar, NotificationType.TASK_ASSIGNED,
                "You were assigned: Payroll calculation module", payrollTask.getId());
        saveNotification(vignesh, NotificationType.COMMENT_ADDED,
                "Praveen Kumar commented on: Payroll calculation module", payrollTask.getId());
        saveNotification(bharathRaj, NotificationType.TASK_ASSIGNED,
                "You were assigned: Payment gateway integration", paymentTask.getId());
    }

    private Project createProject(String name, String desc, String color,
                                   User owner, Set<User> members) {
        Project project = new Project();
        project.setName(name);
        project.setDescription(desc);
        project.setColor(color);
        project.setOwner(owner);
        project.setMembers(members);
        return projectRepository.save(project);
    }

    private void createTask(String title, String desc, TaskStatus status, Priority priority,
                             LocalDate dueDate, int position, Project project,
                             User assignee, User reporter) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(desc);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        task.setPosition(position);
        task.setProject(project);
        task.setAssignee(assignee);
        task.setReporter(reporter);
        taskRepository.save(task);
    }

    private void saveComment(Task task, User author, String content) {
        Comment c = new Comment();
        c.setTask(task);
        c.setAuthor(author);
        c.setContent(content);
        commentRepository.save(c);
    }

    private void saveNotification(User user, NotificationType type, String message, Long refId) {
        Notification n = new Notification();
        n.setUser(user);
        n.setType(type);
        n.setMessage(message);
        n.setReferenceId(refId);
        notificationRepository.save(n);
    }
}
