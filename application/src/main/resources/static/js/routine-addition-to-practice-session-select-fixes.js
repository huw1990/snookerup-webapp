// Fixes a Thymeleaf issue where HTML select elements cannot be selected correctly when bound to an object
window.addEventListener('load', function() {
    const sessionSelector = document.getElementById("sessionSelector");
    const routineSelector = document.getElementById("scoreRoutineSelector");
    if (sessionSelector && routineSelector) {
        console.log("sessionSelector=" + sessionSelector + ", routineSelector=" + routineSelector);
        const searchParams = new URLSearchParams(window.location.search);
        const routineIdToSelect = searchParams.get("routineId");
        const sessionTitleToSelect = searchParams.get("practiceSessionTitle");
        console.log("sessionTitleToSelect=" + sessionTitleToSelect + ", routineIdToSelect=" + routineIdToSelect);
        if (routineIdToSelect) {
            setAsSelectValueIfExistsAsOption(routineIdToSelect, routineSelector);
        }
        if (sessionTitleToSelect) {
            setAsSelectValueIfExistsAsOption(sessionTitleToSelect, sessionSelector);
        }
    }
});

/**
 * Sets the value corresponding to the provided text as the selected option of a HTML select element, as long as the
 * value exists as a possible option in the select.
 * @param textToCheck The text value of the option to select
 * @param selectElement The HTML select element to set the value on
 */
function setAsSelectValueIfExistsAsOption(textToCheck, selectElement) {
    let valueExistsInSelect = false;
    let valueToSet;
    for (let i = 0; i < selectElement.length; i++) {
        if (selectElement.options[i].text === textToCheck) {
            valueExistsInSelect = true;
            valueToSet = selectElement.options[i].value;
        }
    }
    if (valueExistsInSelect) {
        console.log("setting " + selectElement + " value to " + valueToSet);
        selectElement.value = valueToSet;
    }
}