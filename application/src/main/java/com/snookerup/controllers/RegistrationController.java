package com.snookerup.controllers;

import com.snookerup.model.Registration;
import com.snookerup.services.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AliasExistsException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InvalidPasswordException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UsernameExistsException;

/**
 * Controller serving all routes related to user registration.
 *
 * @author Huw
 */
@Controller
@RequestMapping("/register")
public class RegistrationController {

    protected static final String GENERAL_FAILED_REGISTRATION_MESSAGE =
            "Unable to complete registration. Please try again later.";

    protected static final String EMAIL_ALREADY_EXISTS_FAILED_REGISTRATION_MESSAGE =
            "An account with this email address already exists.";

    protected static final String USERNAME_ALREADY_EXISTS_FAILED_REGISTRATION_MESSAGE =
            "Username is already taken.";

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public String getRegisterView(Model model) {
        model.addAttribute("registration", new Registration());
        return "register";
    }

    @PostMapping
    public String registerUser(@Valid Registration registration,
                               BindingResult bindingResult,
                               Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registration", registration);
            return "register";
        }

        try {
            registrationService.registerUser(registration);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully registered for SnookerUp. " +
                            "Please check your email inbox for further instructions."
            );
            redirectAttributes.addFlashAttribute("messageType", "success");

            return "redirect:/";
        } catch (UsernameExistsException e) {
            // Check if message specifies email vs username
            if (e.getMessage() != null && e.getMessage().contains("email")) {
                model.addAttribute("message", EMAIL_ALREADY_EXISTS_FAILED_REGISTRATION_MESSAGE);
            } else {
                model.addAttribute("message", USERNAME_ALREADY_EXISTS_FAILED_REGISTRATION_MESSAGE);
            }
            model.addAttribute("registration", registration);
            model.addAttribute("messageType", "danger");

            return "register";

        } catch (AliasExistsException e) {
            model.addAttribute("registration", registration);
            model.addAttribute("message", EMAIL_ALREADY_EXISTS_FAILED_REGISTRATION_MESSAGE);
            model.addAttribute("messageType", "danger");

            return "register";
        } catch (CognitoIdentityProviderException exception) {
            model.addAttribute("registration", registration);
            model.addAttribute("message", GENERAL_FAILED_REGISTRATION_MESSAGE);
            model.addAttribute("messageType", "danger");

            return "register";
        }
    }
}
