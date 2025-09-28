// Handles the UI elements responsible for searching through all a user's scores
window.addEventListener('load', function() {
    const fromInput = document.getElementById("fromDateTime");
    const toInput = document.getElementById("toDateTime");
    const routineIdSelector = document.getElementById("routineIdSelector");
    console.log("fromInput=" + fromInput + ", toInput=" + toInput + ", routineIdSelector=", routineIdSelector);
    if (fromInput && toInput && routineIdSelector) {
        // Deconstruct the URL to get the individual params
        const queryString = window.location.search;
        console.log(queryString);
        const urlParams = new URLSearchParams(queryString);
        const routineId = urlParams.get('routineId');
        const pageNumber = urlParams.get('pageNumber');
        const from = urlParams.get('from');
        const to = urlParams.get('to');
        const loop = urlParams.get('loop');
        const cushionLimit = urlParams.get('cushionLimit');
        const unitNumber = urlParams.get('unitNumber');
        const potInOrder = urlParams.get('potInOrder');
        const stayOnOneSideOfTable = urlParams.get('stayOnOneSideOfTable');

        // Set the date fields based on the URL inputs
        fromInput.value = from;
        toInput.value = to;

        // Set the variation fields based on the URL inputs
        const loopCheckbox = document.getElementById("loop");
        const cushionLimitCheckbox = document.getElementById("cushionLimit");
        const cushionLimitTextbox = document.getElementById("cushions");
        const unitLimitCheckbox = document.getElementById("redballsLimit");
        const unitLimitTextbox = document.getElementById("redballs");
        const potInOrderCheckbox = document.getElementById("potinorder");
        const stayOnOneSideOfTableCheckbox = document.getElementById("stayononeside");
        if (loop) {
            loopCheckbox.checked = true;
        }
        if (cushionLimit) {
            cushionLimitCheckbox.checked = true;
            cushionLimitTextbox.value = cushionLimit;
        }
        if (unitNumber) {
            unitLimitCheckbox.checked = true;
            unitLimitTextbox.value = unitNumber;
        }
        if (potInOrder) {
            potInOrderCheckbox.checked = true;
        }
        if (stayOnOneSideOfTable) {
            stayOnOneSideOfTableCheckbox.checked = true;
        }

        // Add listeners for the page control buttons
        const firstPageButton = document.getElementById("firstPageButton");
        firstPageButton.addEventListener('click', function() {
            // Change the pageNumber param to 1, which will reload the page
            changePageNumber(1);
        });
        const lastPageButton = document.getElementById("lastPageButton");
        lastPageButton.addEventListener('click', function() {
            // Change the pageNumber param to the last page number (set as a data attr on the button), which will reload the page
            const lastPageNum = lastPageButton.dataset.lastpagenum;
            changePageNumber(lastPageNum);
        });
        const prevPageButton = document.getElementById("prevPageButton");
        prevPageButton.addEventListener('click', function() {
            // Change the pageNumber param to the current number minus 1, which will reload the page
            const newPageNumber = pageNumber - 1;
            changePageNumber(newPageNumber);
        });
        const nextPageButton = document.getElementById("nextPageButton");
        nextPageButton.addEventListener('click', function() {
            // Change the pageNumber param to the current number plus 1, which will reload the page
            const newPageNumber = pageNumber + 1;
            changePageNumber(newPageNumber);
        });

        // Add a listener for the score button
        const searchButton = document.getElementById("searchButton");
        searchButton.addEventListener('click', function() {
            // First validate that from and to dates have been selected. Don't search if they haven't
            const fromDate = fromInput.value;
            const toDate = toInput.value;
            let invalidDateFields = new Array();
            if (!fromDate) {
                invalidDateFields.push(fromInput);
            }
            if (!toDate) {
                invalidDateFields.push(toInput);
            }
            if (invalidDateFields.length > 0) {
                invalidDateFields.forEach((dateField) => {
                    dateField.classList.add('date-error');
                    setTimeout(() => {
                        dateField.classList.remove('date-error');
                    }, 3000);
                    return;
                });
                //Return here so the search isn't actioned
                return;
            }
            // If we get here, the date fields are valid, so continue handling

            // Get the current search params - we'll modify these before sending the request
            const searchParams = new URLSearchParams(window.location.search);

            // Set new dates
            searchParams.set("from", fromDate);
            searchParams.set("to", toDate);

            // Default values for routine variations. If these don't change, we won't add these params
            let loopUrlVal = false;
            let cushionLimitUrlVal = -1;
            let unitNumberUrlVal = -1;
            let potInOrderUrlVal = false;
            let stayOnOneSideUrlVal = false;

            // Get the values from the routine variation elements, then use to set above values
            const routineId = routineIdSelector.value;
            loopUrlVal = loopCheckbox.checked;
            if (cushionLimitCheckbox.checked) {
                cushionLimitUrlVal = cushionLimitTextbox.value;
            }
            if (unitLimitCheckbox.checked) {
                unitNumberUrlVal = unitLimitTextbox.value;
            }
            potInOrderUrlVal = potInOrderCheckbox.checked;
            stayOnOneSideUrlVal = stayOnOneSideOfTableCheckbox.checked;
            if (!loopUrlVal) {
                searchParams.delete('loop');
            } else {
                searchParams.set('loop', 'true');
            }
            if (cushionLimitUrlVal === -1) {
                searchParams.delete('cushionLimit');
            } else {
                searchParams.set('cushionLimit', cushionLimitUrlVal);
            }
            if (unitNumberUrlVal === -1) {
                searchParams.delete('unitNumber');
            } else {
                searchParams.set('unitNumber', unitNumberUrlVal)
            }
            if (!potInOrderUrlVal) {
                searchParams.delete('potInOrder');
            } else {
                searchParams.set('potInOrder', 'true');
            }
            if (!stayOnOneSideUrlVal) {
                searchParams.delete('stayOnOneSideOfTable');
            } else {
                searchParams.set('stayOnOneSideOfTable', 'true');
            }

            // Set updated search params on window location, which will reload the page
            window.location.search = searchParams.toString();
        });
    }
});

function changePageNumber(newPageNumber) {
    const searchParams = new URLSearchParams(window.location.search);
    searchParams.set("pageNumber", newPageNumber);
    window.location.search = searchParams.toString();
}