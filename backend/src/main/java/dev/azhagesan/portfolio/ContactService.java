package dev.azhagesan.portfolio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Service
public class ContactService {
 private final JavaMailSender sender;
 private final boolean enabled;
 private final String from;
 private final String to;
 public ContactService(JavaMailSender sender, @Value("${portfolio.contact.enabled}") boolean enabled,
   @Value("${portfolio.contact.from}") String from, @Value("${portfolio.contact.to}") String to) {
  this.sender=sender; this.enabled=enabled; this.from=from; this.to=to;
 }
 public void send(ContactRequest request) {
  if (request.website()!=null && !request.website().isBlank())
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message could not be sent.");
  if (!enabled || from.isBlank())
   throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "The contact service is unavailable. Please use the email link.");
  var mail = new SimpleMailMessage();
  mail.setFrom(from); mail.setTo(to); mail.setReplyTo(request.email());
  mail.setSubject("Portfolio contact message");
  mail.setText("From: " + request.name() + "\nEmail: " + request.email() + "\n\n" + request.message());
  sender.send(mail);
 }
}
