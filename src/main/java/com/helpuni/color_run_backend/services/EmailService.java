package com.helpuni.color_run_backend.services;

import com.helpuni.color_run_backend.model.Participant;
import com.helpuni.color_run_backend.model.enums.RegistrationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final boolean enabled;
    private final String fromAddress;
    private final String fromName;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.email.enabled:false}") boolean enabled,
                        @Value("${app.email.from}") String fromAddress,
                        @Value("${app.email.from-name:ColourWave Registration Team}") String fromName) {
        this.mailSender = mailSender;
        this.enabled = enabled;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
    }

    public void sendRegistrationReceived(Participant participant) {
        String subject = "ColourWave registration received — " + participant.getParticipantId();
        String content = """
                Hi %s,

                We have received your ColourWave registration and payment receipt.

                Participant ID: %s
                Payment status: Pending manual verification

                The registration team will verify your payment against the bank statement. We will email you once the result is ready.

                Thank you,
                %s
                """.formatted(participant.getStudentName(), participant.getParticipantId(), fromName);
        send(participant.getPersonalEmail(), subject, content);
    }

    public void sendStatusUpdate(Participant participant) {
        if (participant.getRegistrationStatus() == RegistrationStatus.Verified) {
            sendPaymentVerified(participant);
        } else if (participant.getRegistrationStatus() == RegistrationStatus.Rejected) {
            sendPaymentRejected(participant);
        }
    }

    private void sendPaymentVerified(Participant participant) {
        String subject = "ColourWave registration confirmed — " + participant.getParticipantId();
        String content = """
                Hi %s,

                Your payment has been verified and your ColourWave registration is confirmed.

                Participant ID: %s
                Registration status: Verified

                Event details:
                Date: 1 November
                Time: [Event Time]
                Venue: [Event Venue]

                Please keep this email for reference.

                Thank you,
                %s
                """.formatted(participant.getStudentName(), participant.getParticipantId(), fromName);
        send(participant.getPersonalEmail(), subject, content);
    }

    private void sendPaymentRejected(Participant participant) {
        String subject = "Action needed for your ColourWave registration — " + participant.getParticipantId();
        String content = """
                Hi %s,

                We could not verify the payment submitted for your ColourWave registration.

                Participant ID: %s
                Registration status: Rejected
                Admin note: [Admin Note]

                Please contact [Contact Email / Phone] if you believe this was an error or need help submitting the correct payment information.

                Thank you,
                %s
                """.formatted(participant.getStudentName(), participant.getParticipantId(), fromName);
        send(participant.getPersonalEmail(), subject, content);
    }

    private void send(String recipient, String subject, String content) {
        if (!enabled) {
            logger.info("Email sending is disabled. Would send '{}' to {}.", subject, recipient);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromName + " <" + fromAddress + ">");
        message.setTo(recipient);
        message.setSubject(subject);
        message.setText(content);
        try {
            mailSender.send(message);
            logger.info("Email sent: '{}' to {}.", subject, recipient);
        } catch (MailException exception) {
            // A delivery problem must not undo a registration or an admin payment decision.
            logger.error("Could not send '{}' to {}.", subject, recipient, exception);
        }
    }
}
