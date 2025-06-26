window.addEventListener('load', function() {
    const messagesBlock = document.getElementById("messagesBlock");
    if (messagesBlock) {
        // First timeout triggers a CSS transition to fade out the div after giving the user chance to read the message
        setTimeout(() => {
            messagesBlock.classList.remove('show');
            // Second timeout waits for the fade out to complete, then removes the empty space from the display
            setTimeout(() => {
                messagesBlock.style.display = 'none';
            }, 800);
        }, 10000);
    }
});