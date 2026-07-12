// Handles the filtering of and paging through the catalogue of routines, by redirecting with URL params
const routineTagForm = document.getElementById('routineFilterByTagForm');
const searchByTitleInput = document.getElementById('searchByTitle');
const prevPageButton = document.getElementById('prevPageButton');
const nextPageButton = document.getElementById('nextPageButton');
const pageNumberLabel = document.getElementById('pageNumberLabel');
if (routineTagForm && searchByTitleInput && prevPageButton && nextPageButton && pageNumberLabel) {
    // Handle reloading the page, either when paging buttons clicked, a tag button clicked, or Search button click

    // Search by title input
    routineTagForm.addEventListener('submit', event => {
        event.preventDefault();
        // Entering a new search term, so go back to page 0, but keep the current tag
        const searchValue = searchByTitleInput.value;
        const tag = pageNumberLabel.getAttribute('data-tag');
        getRoutines(searchValue, tag, 0);
        return false;
    });

    // Tag buttons
    const tagPillButtons = document.getElementsByClassName('tag-pill');
    Array.prototype.forEach.call(tagPillButtons, function(button) {
        const tagValue = button.getAttribute('data-tag-value');
        button.addEventListener('click', function() {
            const searchValue = searchByTitleInput.value;
            const currentTag = pageNumberLabel.getAttribute('data-tag');
            if (tagValue === currentTag) {
                // Clicked on existing tag, so toggle off, but keep the current search term
                getRoutines(searchValue, null, 0);
            } else {
                // Clicked a tag button, so go back to page 0, but keep the current search term
                getRoutines(searchValue, tagValue, 0);
            }
        });
    });

    prevPageButton.addEventListener('click', event => {
        // Go back a page, but keep search term and tag
        const currentPageNumber = pageNumberLabel.getAttribute('data-current-page');
        const pageToGet = Number(currentPageNumber) - 1;
        const tag = pageNumberLabel.getAttribute('data-tag');
        const searchValue = searchByTitleInput.value;
        getRoutines(searchValue, tag, pageToGet);
    });

    nextPageButton.addEventListener('click', event => {
        // Go forward a page, but keep the search term and tag
        const currentPageNumber = pageNumberLabel.getAttribute('data-current-page');
        const pageToGet = Number(currentPageNumber) + 1;
        const tag = pageNumberLabel.getAttribute('data-tag');
        const searchValue = searchByTitleInput.value;
        getRoutines(searchValue, tag, pageToGet);
    });
}

// Change the page to load a page of routines based on provided parameters
function getRoutines(searchTerm, tag, page) {
    console.log("getRoutines, searchTerm=" + searchTerm + ", tag=" + tag + ", page=" + page);

    // Get the current search params - we'll modify these before sending the request
    const searchParams = new URLSearchParams(window.location.search);

    // Set search term, deleting existing param if none provided
    if (searchTerm) {
        searchParams.set("search", searchTerm);
    } else {
        searchParams.delete("search");
    }

    // Set tag, deleting existing param if none provided
    if (tag && tag !== 'all') {
        searchParams.set("tag", tag);
    } else {
        searchParams.delete("tag");
    }

    // Set page, deleting existing param if none provided
    if (page) {
        searchParams.set("page", page);
    } else {
        searchParams.delete("page");
    }

    // Set updated search params on window location, which will reload the page
    window.location.search = searchParams.toString();
}