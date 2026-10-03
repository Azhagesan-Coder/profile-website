package dev.azhagesan.portfolio;
import jakarta.validation.constraints.*;
public record ContactRequest(
 @NotBlank @Size(max=100) String name,
 @NotBlank @Email @Size(max=254) @Pattern(regexp="^[^\\r\\n]+$") String email,
 @NotBlank @Size(min=10,max=5000) String message,
 @Size(max=200) String website
) {}
