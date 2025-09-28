window.addEventListener('load', function() {
    const deleteScoreButtons = document.getElementsByClassName('deleteScoreButton');
    if (deleteScoreButtons) {
        // Delete the score with a REST request, a DELETE to /scores/{id}
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
        if (csrfHeaderName && csrfToken) {
            reqHeaders.append(csrfHeaderName, csrfToken);
        }
        const reqOptions = {
            method: 'DELETE',
            headers: reqHeaders
        }

        // Add listener to each button
        Array.prototype.forEach.call(deleteScoreButtons, function(button) {
            const idAttr = button.getAttribute('data-scoreid');
            button.addEventListener('click', function() {
                // Use the browser confirm dialog to ensure the user is happy to do the delete
                if (window.confirm("Are you sure you want to delete this score?")) {
                    console.log('Deleting score with ID=' + idAttr);
                    fetch(`scores/${idAttr}`, reqOptions).then(function (response) {
                        console.log('Response code to delete=' + response.status);
                        if (response.status === 204) {
                            return "SUCCESS";
                        } else {
                            return "FAILURE";
                        }
                    }).then(function (resp) {
                        if (resp === "SUCCESS") {
                            // If we received a success response, reload the page, at which point the score should disappear
                            window.location.reload();
                        } else {
                            console.log("Score not deleted, request failed");
                        }
                    }).catch(function (err) {
                        console.warn('Error when trying to delete score with ID=' + idAttr, err);
                    });
                }
            });
        });
    }
});