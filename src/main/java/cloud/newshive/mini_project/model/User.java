package cloud.newshive.mini_project.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class User {

    @Email(message = "Please provide a valid email address.")
    @NotBlank(message = "Email is required.")
    private String email;
    
    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
