window.addEventListener('load', function() {
    const slideNumberButtons = document.getElementsByClassName("slideNumberButton");
    const slidesContainer = document.getElementById("slidesContainer");
    if (slidesContainer && slideNumberButtons.length > 0) {
        const scrollAmount = 300;
        for (let i = 0; i < slideNumberButtons.length; i++) {
            slideNumberButtons[i].addEventListener("click", function() {
                console.log("Clicked index: " + i);
                slidesContainer.scrollTo({
                    left: scrollAmount * i,
                    behavior: "smooth"
                });
            })
        }
    }
});