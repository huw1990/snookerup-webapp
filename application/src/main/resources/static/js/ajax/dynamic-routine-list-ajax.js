const routineTagForm = document.getElementById('routineFilterByTagForm');
if (routineTagForm) {
    routineTagForm.addEventListener('submit', event => {
        event.preventDefault();
        // actual logic, e.g. validate the form
        let selectEl = document.getElementById("tag");
        let tagText = selectEl.options[selectEl.selectedIndex].text;
        fetch(`routines-frag?tag=${tagText}`).then(function (response) {
            return response.text();
        }).then(function (html) {
            let routinesHolderEl = document.getElementById("routinesHolder");
            routinesHolderEl.classList.remove('show');
            setTimeout(() => {
                routinesHolderEl.innerHTML = html;
                routinesHolderEl.classList.add('show');
            }, 600);
        }).catch(function (err) {
            console.warn('Unable to update routines list', err);
        });
        return false;
    });
}