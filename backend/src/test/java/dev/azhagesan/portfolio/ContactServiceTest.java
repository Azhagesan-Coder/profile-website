package dev.azhagesan.portfolio;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
class ContactServiceTest {
 final ContactRequest request = new ContactRequest("Visitor", "visitor@example.com", "Hello, I would like to connect.", "");
 @Test void disabledServiceDoesNotClaimDelivery() {
  var sender=new CapturingSender();
  var service=new ContactService(sender,false,"owner@example.com","owner@example.com");
  assertThrows(ResponseStatusException.class,()->service.send(request)); assertNull(sender.sent);
 }
 @Test void sendsToOwnerAndUsesVisitorAsReplyTo() {
  var sender=new CapturingSender();
  var service=new ContactService(sender,true,"sender@example.com","owner@example.com");
  service.send(request);
  assertEquals("owner@example.com",sender.sent.getTo()[0]);
  assertEquals("visitor@example.com",sender.sent.getReplyTo());
 }
 @Test void propagatesDeliveryFailure() {
  var sender=new CapturingSender(); sender.fail=true;
  assertThrows(MailSendException.class,()->new ContactService(sender,true,"sender@example.com","owner@example.com").send(request));
 }
 static class CapturingSender extends JavaMailSenderImpl {
  SimpleMailMessage sent;
  boolean fail;
  @Override public void send(SimpleMailMessage message) {
   if (fail) throw new MailSendException("failed");
   sent = message;
  }
 }
}
