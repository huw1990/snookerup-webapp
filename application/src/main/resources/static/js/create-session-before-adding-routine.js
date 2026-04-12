// Handles the UI elements responsible for showing the dialog to create a practice session, and the creation of the session itself
window.addEventListener('load', function() {
    const createSessionDialog = document.getElementById("createPracticeSessionDialog");
    const showCreateSessionDialogButton = document.getElementById("createNewPracticeSessionBeforeFromAddToPracticeSessionButton");
    const practiceSessionCreationErrorEl = document.getElementById("practiceSessionCreationErrorMessage");
    if (showCreateSessionDialogButton && createSessionDialog && practiceSessionCreationErrorEl) {
        // Add a listener on the dialog to handle clicking outside the dialog to close
        createSessionDialog.addEventListener("click", (event) => {
            if (event.target === createSessionDialog) {
                createSessionDialog.close();
            }
        });

        // Get the buttons and inputs found in the dialog
        const confirmSessionCreationButton = document.getElementById("confirmPracticeSessionCreationButton");
        const closeDialogButton = document.getElementById("closeDialogButton");
        const sessionTitleInput = document.getElementById("practiceSessionTitle");
        const sessionDescriptionInput = document.getElementById("practiceSessionDescription");

        // Add listener to the confirm button
        confirmSessionCreationButton.addEventListener('click', function() {
            // Create the practice session with a REST request, a POST to /practicesessions
            // To do this, we need to retrieve the CSRF values (the token itself, and the name of the header it should be
            // included in), stored in meta elements on the page, so that they can be included in the request.
            const metaEls = document.getElementsByTagName('meta');
            let csrfToken = null;
            let csrfHeaderName = null;
            Array.prototype.forEach.call(metaEls, function(metaEl) {
                const metaName = metaEl.getAttribute('name');
                const metaContent = metaEl.getAttribute('content');
                if (metaName === '_csrf') {
                    csrfToken = metaContent;
                } else if (metaName === '_csrf_header') {
                    csrfHeaderName = metaContent;
                }
            });
            let reqHeaders = new Headers();
            reqHeaders.append("Content-Type", "application/json");
            if (csrfHeaderName && csrfToken) {
                reqHeaders.append(csrfHeaderName, csrfToken);
            }

            const createSessionBody = {
                "title": sessionTitleInput.value,
                "description": sessionDescriptionInput.value
            }
            const reqOptions = {
                method: 'POST',
                headers: reqHeaders,
                body: JSON.stringify(createSessionBody)
            }

            console.log('Creating session with body=' + JSON.stringify(createSessionBody));
            fetch(`practicesessions`, reqOptions).then(function (response) {
                console.log('Response code to create session=' + response.status);
                return response.json();
            }).then(function (resp) {
                if (resp != null && resp.id != null) {
                    console.log("Session created, ID=" + resp.id);
                    // If we received a success response, reload the page with the new session selected
                    const searchParams = new URLSearchParams(window.location.search);
                    searchParams.set("practiceSessionTitle", resp.title);
                    window.location.search = searchParams.toString();
                } else if (resp != null && resp.error != null) {
                    console.log("Session not created, error=" + resp.error);
                    practiceSessionCreationErrorEl.innerText = resp.error;
                } else {
                    practiceSessionCreationErrorEl.innerText = "";
                    console.log("Session not created, request failed");
                }
            }).catch(function (err) {
                console.warn('Error when trying to create session', err);
            });
        });

        // Add listener to the close button
        closeDialogButton.addEventListener('click', function() {
            createSessionDialog.close();
        });
    }
});