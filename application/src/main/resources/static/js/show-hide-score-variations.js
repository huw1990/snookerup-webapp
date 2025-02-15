window.addEventListener('load', function() {
    const showHideButton = document.getElementById("showHideVariationsButton");
    const variationsBlock = document.getElementById("variationsBlock");
    if (showHideButton && variationsBlock) {
        showHideButton.addEventListener('click', function() {
            // When the menu button is clicked (which is only visible on small screens), show the menu buttons
            variationsBlock.classList.toggle("hidden");
        });
    }
});