package com.esco.etco.config;

import com.esco.etco.entity.Permission;
import com.esco.etco.entity.Role;
import com.esco.etco.entity.User;
import com.esco.etco.repository.PermissionRepository;
import com.esco.etco.repository.RoleRepository;
import com.esco.etco.repository.UserRepository;
import com.esco.etco.util.constant.ApiPaths;
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

            // Permissions
            arr.add(new Permission("Create a permission", ApiPaths.PERMISSIONS_API, "POST", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", ApiPaths.PERMISSIONS_API, "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", ApiPaths.PERMISSIONS_API + "/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", ApiPaths.PERMISSIONS_API + "/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Get permissions with pagination", ApiPaths.PERMISSIONS_API, "GET", "PERMISSIONS"));

            // Roles
            arr.add(new Permission("Create a role", ApiPaths.ROLES_API, "POST", "ROLES"));
            arr.add(new Permission("Update a role", ApiPaths.ROLES_API, "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", ApiPaths.ROLES_API + "/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Get a role by id", ApiPaths.ROLES_API + "/{id}", "GET", "ROLES"));
            arr.add(new Permission("Get roles with pagination", ApiPaths.ROLES_API, "GET", "ROLES"));

            // Users
            arr.add(new Permission("Create a user", ApiPaths.USERS_API, "POST", "USERS"));
            arr.add(new Permission("Delete a user", ApiPaths.USERS_API + "/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Get users with pagination", ApiPaths.USERS_API, "GET", "USERS"));
            arr.add(new Permission("Update a user", ApiPaths.USERS_API, "PUT", "USERS"));
            arr.add(new Permission("Get a user by id", ApiPaths.USERS_API + "/{id}", "GET", "USERS"));

            // Genres
            arr.add(new Permission("Create a Genre", ApiPaths.GENRES_API, "POST", "GENRES"));
            arr.add(new Permission("Update a Genre", ApiPaths.GENRES_API, "PUT", "GENRES"));
            arr.add(new Permission("Delete a Genre", ApiPaths.GENRES_API + "/{id}", "DELETE", "GENRES"));

            // Events
            arr.add(new Permission("Create a Event", ApiPaths.EVENTS_API, "POST", "EVENTS"));
            arr.add(new Permission("Update a Event", ApiPaths.EVENTS_API, "PUT", "EVENTS"));
            arr.add(new Permission("Delete a Event", ApiPaths.EVENTS_API + "/{id}", "DELETE", "EVENTS"));
            arr.add(new Permission("Toggle Active", ApiPaths.EVENTS_API + "/{id}/active", "PATCH", "EVENTS"));
            arr.add(new Permission("Toggle Published", ApiPaths.EVENTS_API + "/{id}/published", "PATCH", "EVENTS"));

            // Tickets
            arr.add(new Permission("Create Tickets", ApiPaths.TICKETS_API, "POST", "TICKETS"));
            arr.add(new Permission("Update Tickets", ApiPaths.TICKETS_API + "/{id}", "PUT", "TICKETS"));

            // Orders
            arr.add(new Permission("Create an order", ApiPaths.CLIENT_ORDERS_API, "POST", "ORDERS"));
            arr.add(new Permission("Pay an order", ApiPaths.CLIENT_ORDERS_API + "/pay", "POST", "ORDERS"));
            arr.add(new Permission("Cancel an order", ApiPaths.CLIENT_ORDERS_API + "/{id}/cancel", "POST", "ORDERS"));
            arr.add(new Permission("Get an order by id", ApiPaths.CLIENT_ORDERS_API + "/{id}", "GET", "ORDERS"));
            arr.add(new Permission("Get orders with pagination", ApiPaths.CLIENT_ORDERS_API, "GET", "ORDERS"));
            arr.add(new Permission("Get my tickets", ApiPaths.CLIENT_ORDERS_API + "/my-tickets", "GET", "ORDERS"));
            arr.add(new Permission("Verify QR Code", ApiPaths.CLIENT_ORDERS_API + "/verify-qr", "POST", "ORDERS"));

            // Files
            arr.add(new Permission("Upload a file", ApiPaths.EVENTS_API + "/{eventId}/images", "POST", "FILES"));
            arr.add(new Permission("Update a file", ApiPaths.EVENTS_API + "/{eventId}/images/{imageId}", "PUT", "FILES"));
            arr.add(new Permission("Delete a file", ApiPaths.EVENTS_API + "/{eventId}/images/{imageId}", "DELETE", "FILES"));

            // Producer Management
            arr.add(new Permission("Create Producer", ApiPaths.PRODUCERS_API, "POST", "PRODUCER"));
            arr.add(new Permission("Update Producer", ApiPaths.PRODUCERS_API + "/{id}", "PUT", "PRODUCER"));
            arr.add(new Permission("Delete Producer", ApiPaths.PRODUCERS_API + "/{id}", "DELETE", "PRODUCER"));
            arr.add(new Permission("Get All Producers", ApiPaths.PRODUCERS_API, "GET", "PRODUCER"));
            arr.add(new Permission("Get Producer by ID", ApiPaths.PRODUCERS_API + "/{id}", "GET", "PRODUCER"));

            // Seat Management
            arr.add(new Permission("Create Seat", ApiPaths.SEATS_API, "POST", "SEAT"));
            arr.add(new Permission("Update Seat", ApiPaths.SEATS_API + "/{id}", "PUT", "SEAT"));
            arr.add(new Permission("Delete Seat", ApiPaths.SEATS_API + "/{id}", "DELETE", "SEAT"));
            arr.add(new Permission("Get Seat by ID", ApiPaths.SEATS_API + "/{id}", "GET", "SEAT"));
            arr.add(new Permission("Get Seats by Event", ApiPaths.SEATS_API + "/event/{eventId}", "GET", "SEAT"));

            // Transaction Management
            arr.add(new Permission("Create Transaction", ApiPaths.TRANSACTIONS_API, "POST", "TRANSACTION"));
            arr.add(new Permission("Update Transaction", ApiPaths.TRANSACTIONS_API + "/{id}", "PUT", "TRANSACTION"));
            arr.add(new Permission("Get All Transactions", ApiPaths.TRANSACTIONS_API, "GET", "TRANSACTION"));
            arr.add(new Permission("Get Transaction by ID", ApiPaths.TRANSACTIONS_API + "/{id}", "GET", "TRANSACTION"));

            // User Ticket Management
            arr.add(new Permission("Create User Ticket", ApiPaths.USER_TICKETS_API, "POST", "USER_TICKET"));
            arr.add(new Permission("Update User Ticket", ApiPaths.USER_TICKETS_API + "/{id}", "PUT", "USER_TICKET"));
            arr.add(new Permission("Get All User Tickets", ApiPaths.USER_TICKETS_API, "GET", "USER_TICKET"));
            arr.add(new Permission("Get User Ticket by ID", ApiPaths.USER_TICKETS_API + "/{id}", "GET", "USER_TICKET"));
            arr.add(new Permission("Get Tickets by User", ApiPaths.USER_TICKETS_API + "/user/{userId}", "GET", "USER_TICKET"));

            // Event Staff Management
            arr.add(new Permission("Add Staff To Event", ApiPaths.EVENT_STAFFS_API, "POST", "EVENT_STAFF"));
            arr.add(new Permission("Remove Staff From Event", ApiPaths.EVENT_STAFFS_API + "/{id}", "DELETE", "EVENT_STAFF"));
            arr.add(new Permission("Get Staffs By Event", ApiPaths.EVENT_STAFFS_API + "/event/{eventId}", "GET", "EVENT_STAFF"));
            arr.add(new Permission("Get Events By Staff", ApiPaths.EVENT_STAFFS_API + "/user/{userId}", "GET", "EVENT_STAFF"));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            createAdminRole(allPermissions);
            createCustomerRole(allPermissions);
            createOrganizerRole(allPermissions);
            createStaffRole(allPermissions);
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

    private void createCustomerRole(List<Permission> allPermissions) {
        List<Permission> customerPermissions = allPermissions.stream()
                .filter(p ->
                        // Quyền xem và cập nhật thông tin user
                        (p.getApiPath().equals(ApiPaths.USERS_API + "/{id}") && p.getMethod().equals("GET")) ||
                                (p.getApiPath().equals(ApiPaths.USERS_API) && p.getMethod().equals("PUT")) ||

                                // Quyền xem events (public)
                                (p.getApiPath().startsWith(ApiPaths.EVENTS_API) && p.getMethod().equals("GET")) ||

                                // Quyền xem genres (public)
                                (p.getApiPath().startsWith(ApiPaths.GENRES_API) && p.getMethod().equals("GET")) ||

                                // Quyền xem tickets (public)
                                (p.getApiPath().startsWith(ApiPaths.TICKETS_API) && p.getMethod().equals("GET")) ||

                                // Quyền xem sơ đồ ghế (public)
                                (p.getApiPath().equals(ApiPaths.SEATS_API + "/event/{eventId}") && p.getMethod().equals("GET")) ||

                                // Quyền đặt hàng, thanh toán, hủy đơn, xem vé đã mua
                                (p.getApiPath().equals(ApiPaths.CLIENT_ORDERS_API) && p.getMethod().equals("POST")) ||
                                (p.getApiPath().equals(ApiPaths.CLIENT_ORDERS_API + "/pay") && p.getMethod().equals("POST")) ||
                                (p.getApiPath().equals(ApiPaths.CLIENT_ORDERS_API + "/{id}/cancel") && p.getMethod().equals("POST")) ||
                                (p.getApiPath().equals(ApiPaths.CLIENT_ORDERS_API + "/{id}") && p.getMethod().equals("GET")) ||
                                (p.getApiPath().equals(ApiPaths.CLIENT_ORDERS_API) && p.getMethod().equals("GET")) ||
                                (p.getApiPath().equals(ApiPaths.CLIENT_ORDERS_API + "/my-tickets") && p.getMethod().equals("GET"))
                )
                .collect(Collectors.toList());

        Role customerRole = new Role();
        customerRole.setName("CUSTOMER");
        customerRole.setDescription("Customer chỉ được xem và cập nhật thông tin cá nhân, đặt vé.");
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
                                p.getModule().equals("SEAT") ||
                                p.getModule().equals("EVENT_STAFF") || // Thêm quyền quản lý nhân viên sự kiện
                                (p.getApiPath().equals(ApiPaths.USERS_API + "/{id}") && p.getMethod().equals("GET")) ||
                                (p.getApiPath().equals(ApiPaths.USERS_API) && p.getMethod().equals("PUT"))
                )
                .collect(Collectors.toList());

        Role organizerRole = new Role();
        organizerRole.setName("ORGANIZER");
        organizerRole.setDescription("Organizer quản lý sự kiện, vé, ghế, nhân viên và thông tin cá nhân");
        organizerRole.setActive(true);
        organizerRole.setPermissions(organizerPermissions);
        this.roleRepository.save(organizerRole);
    }

    private void createStaffRole(List<Permission> allPermissions) {
        List<Permission> staffPermissions = allPermissions.stream()
                .filter(p ->
                        // Quyền xem và cập nhật thông tin user
                        (p.getApiPath().equals(ApiPaths.USERS_API + "/{id}") && p.getMethod().equals("GET")) ||
                                (p.getApiPath().equals(ApiPaths.USERS_API) && p.getMethod().equals("PUT")) ||
                                // Quyền verify QR code
                                (p.getApiPath().equals(ApiPaths.CLIENT_ORDERS_API + "/verify-qr") && p.getMethod().equals("POST"))
                )
                .collect(Collectors.toList());

        Role staffRole = new Role();
        staffRole.setName("STAFF");
        staffRole.setDescription("Staff có quyền quét QR vé sự kiện");
        staffRole.setActive(true);
        staffRole.setPermissions(staffPermissions);
        this.roleRepository.save(staffRole);
    }
}