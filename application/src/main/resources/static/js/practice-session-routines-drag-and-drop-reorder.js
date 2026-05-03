window.addEventListener('load', function() {
    const routineDivs = document.getElementsByClassName('drag-item');
    const container = document.getElementById('drag-container');
    const deleteRoutineButtons = document.getElementsByClassName('deleteRoutineButton');
    const resetButton = document.getElementById('resetButton');
    const saveButton = document.getElementById('saveButton');
    const practiceSessionUpdateErrorEl = document.getElementById("practiceSessionUpdateRoutineOrderErrorMessage");
    if (container && deleteRoutineButtons && resetButton && saveButton && practiceSessionUpdateErrorEl) {
        // Use the third-party lib, Sortable (see: https://github.com/sortablejs/Sortable) to manage the drag-and-drop
        const sortableContainer = new Sortable(container, {
            filter: 'deleteRoutineButton',
            handle: '.dragHandle',
            animation: 150, // Smooth sliding effect (ms)
            ghostClass: 'bg-blue-50', // Tailwind class for the "placeholder" space
            chosenClass: 'opacity-50', // Class applied to the item being dragged
            onEnd: function (evt) {
                console.log(`Moved item from ${evt.oldIndex} to ${evt.newIndex}`);
                // This is where you would call an API to save the new order
            },
        });

        console.log(`Current order=${sortableContainer.toArray()}`);

        // Add a click listener to the delete button on each routine
        Array.prototype.forEach.call(deleteRoutineButtons, function (button) {
            const idAttr = button.getAttribute('data-routine-uuid');
            button.addEventListener('click', function () {
                console.log(`User clicked to remove routine UUID=${idAttr}`);
                let divToRemove = null;
                Array.prototype.forEach.call(routineDivs, function (routineDiv) {
                    if (routineDiv.getAttribute('data-id') === idAttr) {
                        console.log(`Found routine div for UUID=${idAttr}`);
                        divToRemove = routineDiv;
                    }
                });
                if (divToRemove) {
                    const seconds = 1;
                    divToRemove.style.transition = "opacity " + seconds + "s ease";
                    divToRemove.style.opacity = 0;
                    setTimeout(function () {
                        console.log("Removing div=" + divToRemove)
                        divToRemove.parentNode.removeChild(divToRemove);
                    }, seconds * 1000);
                }
            });
        });

        // Add a click listener on the Reset button, which just reloads the page
        resetButton.addEventListener('click', function () {
            console.log("Resetting to saved order by reloading page");
            window.location.reload();
        });

        // Add a click listener on the Save button, which takes the current order of routines and saves them
        saveButton.addEventListener('click', function () {
            console.log('SAVE BUTTON CLICKED!!!!');
            const practiceSessionId = saveButton.getAttribute('data-practice-session-id');
            const currentOrder = sortableContainer.toArray();
            console.log(`Save button clicked, order=${currentOrder}`);

            // Save the current order of routines with a REST request, a PUT to /practicesessions/{id}/editroutines
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
            reqHeaders.append('Content-Type','application/json');
            if (csrfHeaderName && csrfToken) {
                reqHeaders.append(csrfHeaderName, csrfToken);
            }
            const reqBody = {
                uuids: currentOrder
            };
            console.log(`Req body=${JSON.stringify(reqBody)}`);
            const reqOptions = {
                method: 'PUT',
                headers: reqHeaders,
                body: JSON.stringify(reqBody)
            }

            console.log('Updating routines in practice session with ID=' + practiceSessionId);

            fetch(`/practicesessions/${practiceSessionId}/editroutines`, reqOptions).then(function (response) {
                console.log('Response code to update=' + response.status);
                return response.json();
            }).then(function (resp) {
                if (resp != null && resp.id != null) {
                    console.log("Session updated, ID=" + resp.id);
                    window.location = `/practicesessions/${resp.id}`;
                } else if (resp != null && resp.error != null) {
                    console.log("Session not updated, error=" + resp.error);
                    practiceSessionUpdateErrorEl.innerText = resp.error;
                } else {
                    practiceSessionUpdateErrorEl.innerText = "";
                    console.log("Session not updated, request failed");
                }
            }).catch(function (err) {
                console.warn('Error when trying to update routines for practice session with ID=' + practiceSessionId, err);
            });
        });
    }

});