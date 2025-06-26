/*
 * Sets the date field on the score page (if the correct page is loaded) to the current date and time.
 */
window.addEventListener('load', function() {
    const scoreDateTime = document.getElementById("scoreDateTime");
    if (scoreDateTime) {
        const date = new Date();
        const currentDateTime = new Date(date - date.getTimezoneOffset() * 60000);
        // We don't want seconds or millis, so null these out
        currentDateTime.setSeconds(null);
        currentDateTime.setMilliseconds(null);
        scoreDateTime.value = currentDateTime.toISOString().slice(0, -1);
    }
});