window.addEventListener('load', function() {
    const menuButton = document.getElementById("showMobileNavBarButton");
    const navbarButtons = document.getElementById("navbarButtons");
    menuButton.addEventListener('click', function() {
        // When the menu button is clicked (which is only visible on small screens), show the menu buttons
        navbarButtons.classList.toggle("hidden");
    });
});