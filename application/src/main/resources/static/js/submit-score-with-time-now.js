/*
 * Handles the second "Submit" button on the Add Score page, where rather than submitting with the date/time field set
 * as it is, it sets it to the time of submission.
 *
 * This is useful for cases where you have the Add Score page up as you are completing the routines. You submit a score,
 * get redirected back to the Add Score page, then play the next routine and maybe submit the score a couple of minutes
 * afterwards. To avoid having to set the time correctly, you just choose this extra button and the time is automatically
 * fixed for you.
 */
window.addEventListener('load', function() {
    const form = document.getElementById("add-score-form");
    const scoreDateTime = document.getElementById("scoreDateTime");
    if (form && scoreDateTime) {
        form.addEventListener('submit', (event) => {
            // Check which submit button triggered the form submit
            if (event.submitter && event.submitter.id === 'submitTimeNow') {
                const date = new Date();
                const currentDateTime = new Date(date - date.getTimezoneOffset() * 60000);
                // We don't want seconds or millis, so null these out
                currentDateTime.setSeconds(null);
                currentDateTime.setMilliseconds(null);
                scoreDateTime.value = currentDateTime.toISOString().slice(0, -1);
            }
        });
    }
});