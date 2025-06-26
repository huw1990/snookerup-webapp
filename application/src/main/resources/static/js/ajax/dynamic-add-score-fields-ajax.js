// Variables used for functionality in this script
let loopSelected = false;
let scoreUnit = "Break";
let scoreUnitLabel = null;

function setScoreUnit() {
    if (scoreUnitLabel) {
        let scoreUnitLabelValue = "Score";
        if (loopSelected) {
            scoreUnitLabelValue = "Score (Loops)";
        } else if (scoreUnit) {
            scoreUnitLabelValue = "Score (" + scoreUnit + ")";
        }
        scoreUnitLabel.textContent = scoreUnitLabelValue;
    }
}

/**
 * Add listeners for the different variations checkboxes, so that when they're checked/unchecked we can toggle whether
 * the corresponding text input field is enabled/disabled.
 *
 * Note that listeners can't just be added on page load, they instead need to be set whenever the user changes the
 * routine they're adding a score for, because this change uses AJAX to reload the "Variations" section of the Add
 * Score page, meaning brand-new HTML segments that won't have listeners set.
 */
function addListenersForCheckboxTextBoxes() {
    loopSelected = false;
    // Cushion limit
    const cushionLimitCheckbox = document.getElementById("cushionLimit");
    const cushionLimitTextbox = document.getElementById("cushionsNumber");
    if (cushionLimitCheckbox && cushionLimitTextbox) {
        cushionLimitCheckbox.addEventListener('change', function() {
            if (this.checked) {
                cushionLimitTextbox.removeAttribute("disabled");
            } else {
                cushionLimitTextbox.setAttribute("disabled", "true");
            }
        });
    }

    // Unit number
    const unitCheckbox = document.getElementById("varyUnit");
    const unitTextbox = document.getElementById("unit");
    if (unitCheckbox && unitTextbox) {
        unitCheckbox.addEventListener('change', function() {
            if (this.checked) {
                unitTextbox.removeAttribute("disabled");
            } else {
                unitTextbox.setAttribute("disabled", "true");
            }
        });
    }

    // Ball striking
    const ballStrikingCheckbox = document.getElementById("fixedBallStriking");
    const ballStrikingSelector = document.getElementById("ballStrikingSelector");
    if (ballStrikingCheckbox && ballStrikingSelector) {
        ballStrikingCheckbox.addEventListener('change', function() {
            if (this.checked) {
                ballStrikingSelector.removeAttribute("disabled");
            } else {
                ballStrikingSelector.setAttribute("disabled", "true");
            }
        });
    }

    // Loop
    const loopCheckbox = document.getElementById("loop");
    if (loopCheckbox) {
        loopCheckbox.addEventListener('change', function() {
            loopSelected = this.checked;
            setScoreUnit();
        });
    }

    // Score unit
    const variationsDiv = document.getElementById("variationsDiv");
    if (variationsDiv) {
        scoreUnit = variationsDiv.getAttribute("data-score-unit");
        scoreUnitLabel = document.getElementById("scoreUnitLabel");
        setScoreUnit();
    }
}

/*
 * On page load:
 * 1. Enable the listeners on the variations fields with the currently loaded routine.
 * 2. Set up the JavaScript to handle the AJAX functionality whenever the selected routine is changed
 */
addListenersForCheckboxTextBoxes();
const scoreRoutineSelector = document.getElementById('scoreRoutineSelector');
if (scoreRoutineSelector) {
    scoreRoutineSelector.addEventListener('change', event => {
        const selectedRoutineId = scoreRoutineSelector.value;
        fetch(`addscore-variations-frag?routineId=${selectedRoutineId}`).then(function (response) {
            return response.text();
        }).then(function (html) {
            let variationsHolderEl = document.getElementById("variationsHolder");
            variationsHolderEl.classList.remove('show');
            setTimeout(() => {
                variationsHolderEl.innerHTML = html;
                variationsHolderEl.classList.add('show');
                addListenersForCheckboxTextBoxes();
            }, 600);
        }).catch(function (err) {
            console.warn('Unable to update variations', err);
        });
    });
}