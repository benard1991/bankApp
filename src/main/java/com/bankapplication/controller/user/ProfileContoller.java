package com.bankapplication.controller.user;

import com.bankapplication.dto.ChangePasswordRequest;
import com.bankapplication.dto.GenericResponse;
import com.bankapplication.dto.UpdateAccountRequest;
import com.bankapplication.model.User;
import com.bankapplication.service.userService.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileContoller {

private  final UserService userService;

@Autowired
    public ProfileContoller(UserService userService) {
        this.userService = userService;
    }


    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    @GetMapping("/getProfile/{id}")
    public ResponseEntity<GenericResponse> getProfile(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>( user,"User found successfully",HttpStatus.OK.value()));
    }

   @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER')")
    @PostMapping("/changePassword")
    public ResponseEntity<GenericResponse>  changePassword(@RequestBody @Valid ChangePasswordRequest request, Principal principal) {
        GenericResponse response = userService.changePassword(request, principal.getName());
            return ResponseEntity.status(response.getStatus()).body(response);

    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @DeleteMapping("/deleteAccount/{userId}")
    public ResponseEntity<GenericResponse> deleteUser(@PathVariable Long userId) {
        GenericResponse response = userService.deleteUser(userId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }




}
