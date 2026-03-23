package com.esco.etco.config;

import com.esco.etco.entity.Permission;
import com.esco.etco.entity.Role;
import com.esco.etco.entity.User;
import com.esco.etco.repository.PermissionRepository;
import com.esco.etco.repository.RoleRepository;
import com.esco.etco.repository.UserRepository;
import com.esco.etco.util.constant.GenderEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        log.error(">>> START INIT DATABASE");
        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();

            arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));

            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
            arr.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));

            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));

            arr.add(new Permission("Create a Genre", "/api/v1/genres", "POST", "GENRES"));
            arr.add(new Permission("Update a Genre", "/api/v1/genres", "PUT", "GENRES"));
            arr.add(new Permission("Delete a Genre", "/api/v1/genres/{id}", "DELETE", "GENRES"));
            arr.add(new Permission("Get a Genre by id", "/api/v1/genres/{id}", "GET", "GENRES"));
            arr.add(new Permission("Get Genre with pagination", "/api/v1/genres", "GET", "GENRES"));

            arr.add(new Permission("Create a Event", "/api/v1/events", "POST", "EVENTS"));
            arr.add(new Permission("Update a Event", "/api/v1/events", "PUT", "EVENTS"));
            arr.add(new Permission("Delete a Event", "/api/v1/events/{id}", "DELETE", "EVENTS"));
            arr.add(new Permission("Get a Event by id", "/api/v1/events/{id}", "GET", "EVENTS"));
            arr.add(new Permission("Get Event with pagination", "/api/v1/events", "GET", "EVENTS"));
            arr.add(new Permission("Toggle Active", "/api/v1/events/{id}/active", "PATCH", "EVENTS"));
            arr.add(new Permission("Toggle Published", "/api/v1/events/{id}/published", "PATCH", "EVENTS"));

            arr.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));

            arr.add(new Permission("Create Tickets", "/api/v1/tickets", "POST", "TICKETS"));
            arr.add(new Permission("Update Tickets", "/api/v1/tickets/{id}", "PUT", "TICKETS"));
            arr.add(new Permission("Get a Tickets", "/api/v1/tickets/{id}", "GET", "TICKETS"));
            arr.add(new Permission("Get Tickets with pagination", "/api/v1/tickets", "GET", "TICKETS"));

            arr.add(new Permission("Create an order", "/api/v1/orders", "POST", "ORDERS"));
            arr.add(new Permission("Pay an order", "/api/v1/orders/pay", "POST", "ORDERS"));
            arr.add(new Permission("Get an order by id", "/api/v1/orders/{id}", "GET", "ORDERS"));
            arr.add(new Permission("Get orders with pagination", "/api/v1/orders", "GET", "ORDERS"));
            arr.add(new Permission("Get my tickets", "/api/v1/orders/my-tickets", "GET", "ORDERS"));
            arr.add(new Permission("Verify QR Code", "/api/v1/orders/verify-qr", "POST", "ORDERS"));

            arr.add(new Permission("Upload a file", "/api/v1/events/{eventId}/images", "POST", "FILES"));
            arr.add(new Permission("Update a file", "/api/v1/events/{eventId}/images/{imageId}", "PUT", "FILES"));
            arr.add(new Permission("Delete a file", "/api/v1/events/{eventId}/images/{imageId}", "DELETE", "FILES"));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            createAdminRole(allPermissions);
            createCustomerRole(allPermissions);
            createOrganizerRole(allPermissions);
        }

        if (countUsers == 0) {
            User adminUser = new User();
            adminUser.setEmail("admin@gmail.com");
            adminUser.setAddress("hcm");
            adminUser.setAge(25);
            adminUser.setGender(GenderEnum.MALE);
            adminUser.setName("I'm super admin");
            adminUser.setPassword(this.passwordEncoder.encode("123456"));

            Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
            if (adminRole != null) {
                adminUser.setRole(adminRole);
            }

            this.userRepository.save(adminUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            log.error(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            log.error(">>> END INIT DATABASE");
    }

    private void createAdminRole(List<Permission> allPermissions) {
        Role adminRole = new Role();
        adminRole.setName("SUPER_ADMIN");
        adminRole.setDescription("Admin có full permissions");
        adminRole.setActive(true);
        adminRole.setPermissions(allPermissions);
        this.roleRepository.save(adminRole);
    }

    // từ đây trở xuống là để test không phải data thật
    private void createCustomerRole(List<Permission> allPermissions) {
        List<Permission> customerPermissions = allPermissions.stream()
                .filter(p ->
                        // Quyền xem và cập nhật thông tin user
                        (p.getApiPath().equals("/api/v1/users/{id}") && p.getMethod().equals("GET")) ||
                                (p.getApiPath().equals("/api/v1/users") && p.getMethod().equals("PUT")) ||

                                // Quyền xem events
                                (p.getApiPath().equals("/api/v1/events") && p.getMethod().equals("GET")) ||
                                (p.getApiPath().equals("/api/v1/events/{id}") && p.getMethod().equals("GET")) ||

                                // Quyền xem genres
                                (p.getApiPath().equals("/api/v1/genres") && p.getMethod().equals("GET")) ||
                                (p.getApiPath().equals("/api/v1/genres/{id}") && p.getMethod().equals("GET")) ||

                                // Quyền xem tickets
                                (p.getApiPath().equals("/api/v1/tickets") && p.getMethod().equals("GET")) ||
                                (p.getApiPath().equals("/api/v1/tickets/{id}") && p.getMethod().equals("GET")) ||

                                // ✅ MỚI: Quyền đặt hàng, thanh toán, xem vé đã mua
                                (p.getApiPath().equals("/api/v1/orders") && p.getMethod().equals("POST")) ||
                                (p.getApiPath().equals("/api/v1/orders/pay") && p.getMethod().equals("POST")) ||
                                (p.getApiPath().equals("/api/v1/orders/{id}") && p.getMethod().equals("GET")) ||
                                (p.getApiPath().equals("/api/v1/orders") && p.getMethod().equals("GET")) ||
                                (p.getApiPath().equals("/api/v1/orders/my-tickets") && p.getMethod().equals("GET"))
                )
                .collect(Collectors.toList());

        Role customerRole = new Role();
        customerRole.setName("CUSTOMER");
        customerRole.setDescription("Customer chỉ được xem và cập nhật thông tin cá nhân");
        customerRole.setActive(true);
        customerRole.setPermissions(customerPermissions);
        this.roleRepository.save(customerRole);
    }

    private void createOrganizerRole(List<Permission> allPermissions) {
        List<Permission> organizerPermissions = allPermissions.stream()
                .filter(p ->
                        p.getModule().equals("EVENTS") ||
                                p.getModule().equals("FILES") ||
                                p.getModule().equals("TICKETS") ||
                                (p.getApiPath().equals("/api/v1/users/{id}") && p.getMethod().equals("GET")) ||
                                (p.getApiPath().equals("/api/v1/users") && p.getMethod().equals("PUT"))
                )
                .collect(Collectors.toList());

        Role organizerRole = new Role();
        organizerRole.setName("ORGANIZER");
        organizerRole.setDescription("Organizer quản lý sự kiện và thông tin cá nhân");
        organizerRole.setActive(true);
        organizerRole.setPermissions(organizerPermissions);
        this.roleRepository.save(organizerRole);
    }
}