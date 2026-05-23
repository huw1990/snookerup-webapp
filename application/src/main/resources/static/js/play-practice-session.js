// Handles the UI elements responsible for submitting scores for a whole practice session
window.addEventListener('load', function() {
    const scoreInputs = document.getElementsByClassName("scoreInput");
    const deleteScoreButtons = document.getElementsByClassName("unsetScoreButton");
    const saveScoresButton = document.getElementById("submitScoresButton");
    const resetScoresButton = document.getElementById("resetScoresButton");

    if (saveScoresButton && resetScoresButton) {
        // For each score input, add listeners for the various ways the text input can change
        Array.prototype.forEach.call(scoreInputs, function(scoreInput) {
            scoreInput.addEventListener('change', scoreInputChanged);
            scoreInput.addEventListener('keypress', scoreInputChanged);
            scoreInput.addEventListener('paste', scoreInputChanged);
            scoreInput.addEventListener('input', scoreInputChanged);
        });

        // Add a click listener for each unset score button
        Array.prototype.forEach.call(deleteScoreButtons, function(button) {
            button.addEventListener('click', unsetScore);
        });

        // Add a click listener for the Save button
        saveScoresButton.addEventListener('click', submitScoresButtonClicked);

        // Add a click listener for the Reset button
        resetScoresButton.addEventListener('click', resetAllFields);
    }

});

// Handles the user clicking the Submit button
function submitScoresButtonClicked() {
    // Start by getting details of how many attempts have been made
    const progress = getProgress();

    if (progress.numberAttempted == progress.totalRoutines) {
        console.log("All routines attempted, so submitting scores immediately");
        confirmSubmitScores();
    } else {
        // Not all attempts made, so ask user to confirm submission
        showSubmitScoresDialog(progress);
    }
}

// Handles showing a dialog to the user to confirm they want to submit the scores as-is
function showSubmitScoresDialog(progress) {
    console.log("Not all routines attempted, showing modal for user to confirm submit");

    const numberAttemptedLabel = document.getElementById("numberAttemptedLabel");
    const yesButton = document.getElementById("yesButton");
    const noButton = document.getElementById("noButton");
    const confirmSubmitScoresDialog = document.getElementById("confirmSubmitScoresDialog");

    // Add a listener on the dialog to handle clicking outside the dialog to close
    confirmSubmitScoresDialog.addEventListener("click", (event) => {
        if (event.target === confirmSubmitScoresDialog) {
            confirmSubmitScoresDialog.close();
        }
    });

    // Add a listener on the "No" button, which just closes the dialog
    noButton.addEventListener("click", (event) => {
        confirmSubmitScoresDialog.close();
    });

    // Add a listener on the "Yes button, which submits the scores
    yesButton.addEventListener("click", (event) => {
        confirmSubmitScoresDialog.close();
        confirmSubmitScores();
    });

    // Update the progress label
    numberAttemptedLabel.innerText = "You have only attempted " + progress.numberAttempted + " of "
        + progress.totalRoutines + " routines in this practice session.";

    // Show the dialog
    window.confirmSubmitScoresDialog.showModal();
}

// Handles the actual submitting of scores to the backend using a REST API
function confirmSubmitScores() {
    // First, get the score input fields and work out which have been completed
    const scoreInputs = document.getElementsByClassName("scoreInput");
    const inputsWithScores = [];
    Array.prototype.forEach.call(scoreInputs, function(input) {
        if (input.value != "") {
            inputsWithScores.push(input);
        }
    });

    // Now collect the routine UUID, score, note, and time for the submitted score, in an array
    const scoreInfos = [];
    Array.prototype.forEach.call(inputsWithScores, function(input) {
        const uuid = input.getAttribute("data-uuid");
        const attemptNum = input.getAttribute("data-attempt-num");
        const uuidAndAttemptNumberElements = getElementsWithSameRoutineUuidAndAttemptNumber(uuid, attemptNum);
        scoreInfos.push({
            score: input.value,
            note: uuidAndAttemptNumberElements.noteInput.value,
            routineUuid: uuid,
            dateTimeString: uuidAndAttemptNumberElements.dateTimeLabel.innerText
        });
    });

    // Group the scores by routine UUID, so for each UUID we have a list of scores to submit
    const grouped = Object.groupBy(scoreInfos, item => item.routineUuid);

    // Construct the request body JSON as is required by the REST API to submit scores
    const submitScoresBody = {
        routinesWithScores: Object.entries(grouped).map(([routineUuid, items]) => ({
            routineUuid,
            scores: items.map(({ score, note, dateTimeString }) => ({ score, note, dateTimeString }))
        }))
    };

    // Submit the score using the REST API
    const practiceSessionId = document.getElementById("submitScoresButton").getAttribute("data-practice-session-id");
    // Submit the scores with a REST request, a POST to /practicesessions/<ID>/play
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
    const reqOptions = {
        method: 'POST',
        headers: reqHeaders,
        body: JSON.stringify(submitScoresBody)
    }
    console.log(`Submitting scores for practice session with ID=${practiceSessionId} and body=` + JSON.stringify(submitScoresBody));
    practiceSessionPlayErrorMessage = document.getElementById("practiceSessionPlayErrorMessage");
    const genericError = "Sorry, something went wrong"
    fetch(`/practicesessions/${practiceSessionId}/play`, reqOptions).then(function (response) {
        console.log('Response code to create session=' + response.status);
        return response.json();
    }).then(function (resp) {
        if (resp != null && resp.ids != null) {
            console.log("Session created, ID=" + resp.id);
            // If we received a success response, redirect to the scores page
            // window.location = "/scores";
        } else if (resp != null && resp.error != null) {
            console.log("Session scores not submitted, error=" + resp.error);
            practiceSessionPlayErrorMessage.innerText = genericError;
        } else {
            console.log("Session scores not submitted, request failed");
            practiceSessionPlayErrorMessage.innerText = genericError;
        }
    }).catch(function (err) {
        console.warn('Error when trying to submit session scores', err);
        practiceSessionPlayErrorMessage.innerText = genericError;
    });
}

// Resets all fields to how they were when the page first loaded.
function resetAllFields() {
    const scoreInputs = document.getElementsByClassName("scoreInput");
    const noteInputs = document.getElementsByClassName("noteInput");
    const dateTimeLabels = document.getElementsByClassName("dateTimeLabel");
    const unsetScoreButtons = document.getElementsByClassName("unsetScoreButton");
    Array.prototype.forEach.call(scoreInputs, function(input) {
        input.value = "";
    });
    Array.prototype.forEach.call(noteInputs, function(input) {
        input.value = "";
    });
    Array.prototype.forEach.call(dateTimeLabels, function(label) {
        label.innerText = "--/--/-- --:--";
    });
    Array.prototype.forEach.call(unsetScoreButtons, function(button) {
        button.disabled = true;
    });
    updateProgress();
}

/*
 * Handles any change to a score input field. Either the user is setting a value, in which case we set the time and
 * allow the score to be unset, otherwise we reset the time field and prevent using the button to unset the score.
 */
function scoreInputChanged(event) {
    const inputValue = event.target.value;
    const elsWithSameUuid = getElementsWithSameRoutineUuidAndAttemptNumberFromElement(event.target);
    if (elsWithSameUuid.unsetScoreButton != null && elsWithSameUuid.dateTimeLabel != null) {
        let disableButton;
        if (inputValue === "") {
            disableButton = true;
            elsWithSameUuid.dateTimeLabel.removeAttribute("data-millis");
            elsWithSameUuid.dateTimeLabel.innerText = "--/--/-- --:--";
        } else {
            disableButton = false;
            const dateTime = new Date().toLocaleString();
            elsWithSameUuid.dateTimeLabel.innerText = dateTime;
        }
        elsWithSameUuid.unsetScoreButton.disabled = disableButton;
        updateProgress();
    }
}

/*
 * Unsets a particular score. Clears the score input field, removes the time, and disables the unset score button.
 */
function unsetScore(event) {
    const elsWithSameUuid = getElementsWithSameRoutineUuidAndAttemptNumberFromElement(event.target);
    if (elsWithSameUuid.unsetScoreButton != null && elsWithSameUuid.dateTimeLabel != null
                && elsWithSameUuid.scoreInput != null) {
        elsWithSameUuid.unsetScoreButton.disabled = true;
        elsWithSameUuid.dateTimeLabel.innerText = "--/--/-- --:--";
        elsWithSameUuid.scoreInput.value = "";
        elsWithSameUuid.noteInput.value = "";
        updateProgress();
    }
}

/*
 * Takes an element as an input, then queries data attributes on the element to get the routine UUID and attempts
 * number, and uses those to find all the other corresponding elements (score input, note input, date/time label,
 * unset score button), returning those in a single object.
 */
function getElementsWithSameRoutineUuidAndAttemptNumberFromElement(element) {
    const uuid = element.getAttribute("data-uuid");
    const attemptNumber = element.getAttribute("data-attempt-num");
    return getElementsWithSameRoutineUuidAndAttemptNumber(uuid, attemptNumber);
}

/*
 * Takes a routine UUID and attempt number, then finds all the other corresponding elements (score input, note input,
 * date/time label, unset score button), returning those in a single object.
 */
function getElementsWithSameRoutineUuidAndAttemptNumber(uuid, attemptNumber) {
    const scoreInputs = document.getElementsByClassName("scoreInput");
    const noteInputs = document.getElementsByClassName("noteInput");
    const dateTimeLabels = document.getElementsByClassName("dateTimeLabel");
    const unsetScoreButtons = document.getElementsByClassName("unsetScoreButton");
    let scoreInput = null;
    let noteInput = null;
    let dateTimeLabel = null;
    let unsetScoreButton = null;
    Array.prototype.forEach.call(scoreInputs, function(input) {
        if (input.getAttribute("data-uuid") === uuid
            && input.getAttribute("data-attempt-num") === attemptNumber) {
            scoreInput = input;
        }
    });
    Array.prototype.forEach.call(noteInputs, function(input) {
        if (input.getAttribute("data-uuid") === uuid
            && input.getAttribute("data-attempt-num") === attemptNumber) {
            noteInput = input;
        }
    });
    Array.prototype.forEach.call(dateTimeLabels, function(label) {
        if (label.getAttribute("data-uuid") === uuid
            && label.getAttribute("data-attempt-num") === attemptNumber) {
            dateTimeLabel = label;
        }
    });
    Array.prototype.forEach.call(unsetScoreButtons, function(button) {
        if (button.getAttribute("data-uuid") === uuid
            && button.getAttribute("data-attempt-num") === attemptNumber) {
            unsetScoreButton = button;
        }
    });
    return {
        scoreInput, noteInput, dateTimeLabel, unsetScoreButton
    };
}

/*
 * Update the label at the bottom of the page to show the user how many of the attempts have been completed.
 */
function updateProgress() {
    const progress = getProgress();
    const progressLabel = document.getElementById("playPracticeSessionProgressLabel");
    progressLabel.innerText = progress.numberAttempted + " of " + progress.totalRoutines + " attempts completed"
}

// Gets the user's current progress, i.e. how many attempts have been made, and the total attempts possible
function getProgress() {
    const unsetScoreButtons = document.getElementsByClassName("unsetScoreButton");
    let numberAttempted = 0;
    Array.prototype.forEach.call(unsetScoreButtons, function(button) {
        if (!button.disabled) {
            numberAttempted++;
        }
    });
    return {
        numberAttempted,
        totalRoutines: unsetScoreButtons.length
    }
}