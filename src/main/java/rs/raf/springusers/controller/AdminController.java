package rs.raf.springusers.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.raf.springusers.dto.EditUserRequest;
import rs.raf.springusers.dto.RoleRequest;
import rs.raf.springusers.dto.SignUpRequest;
import rs.raf.springusers.entities.Role;
import rs.raf.springusers.entities.User;
import rs.raf.springusers.services.AuthenticationService;
import rs.raf.springusers.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    @GetMapping
    public ResponseEntity<String> hello()
    {
        return ResponseEntity.ok("helloo admin");
    }

    @PostMapping("/{user_id}/change_role")
    public ResponseEntity<User> changeRole(@PathVariable Long user_id, @RequestBody RoleRequest roleRequest)
    {

        User user = userService.changeRole(user_id, roleRequest.getRole());
        if(user==null)
        {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/get-users")
    public ResponseEntity<List<User>> getUsers()
    {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/{user_id}/update-user")
    public ResponseEntity<User> updateUser(@PathVariable Long user_id,@RequestBody EditUserRequest sign)
    {
        User user = userService.updateUser(user_id, sign);
        if(user == null)
        {
            return  ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{user_id}/delete-user")
    public ResponseEntity<String> deleteUser(@PathVariable Long user_id)
    {
        userService.deleteUser(user_id);
        return ResponseEntity.ok("lala");
    }

    @PostMapping("/create-user")
    public ResponseEntity<User> signUp(@RequestBody SignUpRequest sign)
    {
        return ResponseEntity.ok(authenticationService.signUp(sign));
    }


}
