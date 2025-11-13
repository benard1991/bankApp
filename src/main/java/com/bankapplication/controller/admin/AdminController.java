package com.bankapplication.controller.admin;

import com.bankapplication.dto.GenericResponse;
import com.bankapplication.dto.UpdateAccountRequest;
import com.bankapplication.util.PaginationInfo;
import com.bankapplication.dto.CustomPageResponse;
import com.bankapplication.model.User;
import com.bankapplication.service.userService.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private  final UserService userService;


    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/getAllUser")
    public ResponseEntity<GenericResponse<CustomPageResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    )
    {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<User> userPage = userService.getAllUsers(pageable);

        PaginationInfo paginationInfo = new PaginationInfo(
                userPage.getSize(),
                userPage.getNumber(),
                userPage.getTotalPages(),
                userPage.getTotalElements()
        );

        CustomPageResponse response = new CustomPageResponse(userPage.getContent(), paginationInfo);
        return ResponseEntity.ok(new GenericResponse<>(response, "Users retrieved successfully", 200));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/deactivateUser/{userId}")
    public ResponseEntity<GenericResponse<User>> deactivateUser(@PathVariable Long userId) {
        User deactivatedUser = userService.deactivateUser(userId);
        return ResponseEntity.ok(new GenericResponse<>(deactivatedUser, "User deactivated successfully", HttpStatus.OK.value()));
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/activateUser/{userId}")
    public ResponseEntity<GenericResponse<User>> activeUser(@PathVariable Long userId) {
        User deactivatedUser = userService.activateUser(userId);
        return ResponseEntity.ok(new GenericResponse<>(deactivatedUser, "User activated successfully", HttpStatus.OK.value()));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/updateAccount/{userId}")
    public ResponseEntity<GenericResponse> updateUser(@PathVariable Long userId,@RequestBody @Valid UpdateAccountRequest request) {
        User updatedUser = userService.updateUserProfile(userId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>( updatedUser,"Update successfully",HttpStatus.OK.value()));
    }


}
