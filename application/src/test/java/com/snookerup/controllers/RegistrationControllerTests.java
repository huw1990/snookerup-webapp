package com.snookerup.controllers;

import com.snookerup.model.Registration;
import com.snookerup.services.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RegistrationController class.
 *
 * @author Huw
 */
class RegistrationControllerTests {

    private static final String REGISTER_PAGE = "register";
    private static final String REDIRECT_TO_HOMEPAGE = "redirect:/";
    private static final String USERNAME = "username";
    private static final String EMAIL = "test@example.com";
    private static final String VALID_INVITATION_CODE = "CRUCIBLE";
    private static final String INVALID_INVITATION_CODE = "TEST";

    private RegistrationService mockRegistrationService;
    private Model mockModel;
    private BindingResult mockBindingResult;
    private RedirectAttributes mockRedirectAttributes;
    private CognitoIdentityProviderException mockCognitoException;

    RegistrationController registrationController;

    @BeforeEach
    public void beforeEach() {
        mockRegistrationService = mock(RegistrationService.class);
        mockModel = mock(Model.class);
        mockBindingResult = mock(BindingResult.class);
        mockRedirectAttributes = mock(RedirectAttributes.class);
        mockCognitoException = mock(CognitoIdentityProviderException.class);

        registrationController = new RegistrationController(mockRegistrationService);
    }

    @Test
    public void getRegisterView_Should_ReturnRegisterWithEmptyRegistration() {
        // Define variables

        // Set mock expectations

        // Execute method under test
        String returnedPage = registrationController.getRegisterView(mockModel);

        // Verify
        assertEquals(REGISTER_PAGE, returnedPage);
        verify(mockModel).addAttribute("registration", new Registration());
    }

    @Test
    public void registerUser_Should_ReturnToRegisterPageWithRegistration_When_BindingResultHasErrors() {
        // Define variables
        Registration invalidRegistration = new Registration();
        invalidRegistration.setUsername(USERNAME);
        invalidRegistration.setEmail(EMAIL);
        invalidRegistration.setInvitationCode(INVALID_INVITATION_CODE);

        // Set mock expectations
        when(mockBindingResult.hasErrors()).thenReturn(true);

        // Execute method under test
        String page = registrationController.registerUser(invalidRegistration,
                mockBindingResult,
                mockModel,
                mockRedirectAttributes);

        // Verify
        assertEquals(REGISTER_PAGE, page);
        verify(mockModel).addAttribute("registration", invalidRegistration);
    }

    @Test
    public void registerUser_Should_ReturnToRegisterPageWithRegistrationAndErrorMessage_When_RegistrationServiceThrowsException() {
        // Define variables
        Registration validRegistration = new Registration();
        validRegistration.setUsername(USERNAME);
        validRegistration.setEmail(EMAIL);
        validRegistration.setInvitationCode(VALID_INVITATION_CODE);
        String cognitoExceptionMessage = "Cognito exception";

        // Set mock expectations
        doThrow(mockCognitoException).when(mockRegistrationService).registerUser(validRegistration);
        when(mockCognitoException.getMessage()).thenReturn(cognitoExceptionMessage);

        // Execute method under test
        String page = registrationController.registerUser(validRegistration,
                mockBindingResult,
                mockModel,
                mockRedirectAttributes);

        // Verify
        assertEquals(REGISTER_PAGE, page);
        verify(mockModel).addAttribute("registration", validRegistration);
        verify(mockModel).addAttribute("message", cognitoExceptionMessage);
        verify(mockModel).addAttribute("messageType", "danger");
    }

    @Test
    public void registerUser_Should_RedirectToHomePageWithSuccessMessage_When_RegistrationSucceeds() {
        // Define variables
        Registration validRegistration = new Registration();
        validRegistration.setUsername(USERNAME);
        validRegistration.setEmail(EMAIL);
        validRegistration.setInvitationCode(VALID_INVITATION_CODE);

        // Set mock expectations

        // Execute method under test
        String page = registrationController.registerUser(validRegistration,
                mockBindingResult,
                mockModel,
                mockRedirectAttributes);

        // Verify
        assertEquals(REDIRECT_TO_HOMEPAGE, page);
        verify(mockRedirectAttributes).addFlashAttribute("message",
                "You successfully registered for SnookerUp. Please check your email inbox for further instructions.");
        verify(mockRedirectAttributes).addFlashAttribute("messageType", "success");
    }
}
