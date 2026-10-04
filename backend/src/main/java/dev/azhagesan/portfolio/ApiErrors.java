package dev.azhagesan.portfolio;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.mail.MailException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice
public class ApiErrors {
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<?> validation() { return ResponseEntity.badRequest().body(Map.of("message", "Please check your name, email and message.")); }
 @ExceptionHandler(ResponseStatusException.class)
 public ResponseEntity<?> status(ResponseStatusException e) { return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason()==null ? "Request failed." : e.getReason())); }
 @ExceptionHandler(MailException.class)
 public ResponseEntity<?> mail() { return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of("message", "Email delivery failed. Please try again or use the email link.")); }
}
