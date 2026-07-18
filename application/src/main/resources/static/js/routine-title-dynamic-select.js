// Handles the user selecting a routine dynamically by polling the backend REST API
const routineIdSelector = document.getElementById('routineIdSelector');
if (routineIdSelector) {
    new TomSelect('#routineIdSelector', {
        valueField: 'routineId',    // From the chosen returned JSON object, this field maps to the select value
        labelField: 'title',        // From the chosen returned JSON object, this field maps to the display of the selected item
        searchField: [],            // Empty array disables local filtering so it shows exactly what the backend API returns
        plugins: ['virtual_scroll', 'clear_button'],
        maxOptions: 200,

        // Set what the first URL should be when searching using the API
        firstUrl: function(query) {
            return `/routine-overviews?search=${encodeURIComponent(query)}&page=0&size=15`;
        },

        // Handles the loading of data from the API, potentially in multiple pages
        load: function(query, callback) {
            // Gets the URL to use. For the first call, see firstUrl above, otherwise see how subsequent requests
            // are set using setNextUrl()
            const url = this.getUrl(query);

            fetch(url)
                .then(response => response.json())
                .then(json => {
                    const pageInfo = json.page; // Get the page info, so we can build what will be the next URL
                    const content = json.content;      // The actual content is in a "content" field in the JSON response

                    // Check if there are more pages available
                    if (pageInfo && (pageInfo.number + 1 < pageInfo.totalPages)) {
                        const nextPage = pageInfo.number + 1;
                        const nextUrl = `/routine-overviews?search=${encodeURIComponent(query)}&page=${nextPage}&size=15`;

                        this.setNextUrl(query, nextUrl);
                    }

                    // Pass the content array to Tom Select
                    callback(content);
                })
                .catch(() => {
                    callback();
                });
        },

        // Custom HTML render templates matching your payload fields
        render: {
            option: function(item, escape) {
                return `<div>${escape(item.title)}</div>`;
            },
            item: function(item, escape) {
                return `<div>${escape(item.title)}</div>`;
            },
            loading_more: function(data, escape) {
                return `<div class="loading-more-results">Loading more...</div>`;
            },
            no_more_results: function(data, escape) {
                return `<div class="no-more-results">All routines loaded</div>`;
            },
            no_results: function(data, escape) {
                return `<div class="no-results">No results found for "${escape(data.clean)}"</div>`;
            }
        }
    });
}