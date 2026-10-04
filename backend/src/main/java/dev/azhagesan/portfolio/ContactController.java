package dev.azhagesan.portfolio;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/contact")
public class ContactController {
 private final ContactService service;
 public ContactController(ContactService service) { this.service=service; }
 @PostMapping public Map<String,String> contact(@Valid @RequestBody ContactRequest request) {
  service.send(request); return Map.of("message", "Your message has been sent. Thank you for getting in touch.");
 }
}
