window.addEventListener('load', function() {
    const heroTitle = document.getElementById("heroTitle");
    if (heroTitle) {
        // Changes the colour of the title text in the homepage hero periodically, cycling through the main snooker
        // table colours
        const redRgb = {r:239, g:68, b:68};
        const yellowRgb = {r:247, g:234, b:49};
        const greenRgb = {r:6, g:148, b:8};
        const brownRgb = {r:125, g:78, b:4};
        const blueRgb = {r:16, g:118, b:196};
        const pinkRgb = {r:217, g:143, b:213};
        const blackRgb = {r:1, g:1, b:1};
        const coloursInOrder = [redRgb, yellowRgb, greenRgb, brownRgb, blueRgb, pinkRgb, blackRgb];
        const lerp = function(a,b,u) {
            return (1-u) * a + u * b;
        };
        const fade = function(element, start, end, duration) {
            if (document.hidden) {
                // Don't change colour while tab isn't active
                return;
            }
            let interval = 10;
            let steps = duration/interval;
            let step_u = 1.0/steps;
            let u = 0.0;
            currentColourIndex++;
            let theInterval = setInterval(function(){
                if (u >= 1.0){ clearInterval(theInterval) }
                let r = parseInt(lerp(start.r, end.r, u));
                let g = parseInt(lerp(start.g, end.g, u));
                let b = parseInt(lerp(start.b, end.b, u));
                let colorname = 'rgb('+r+','+g+','+b+')';
                element.style.color = colorname;
                u += step_u;
            }, interval);
        };
        const transitionDuration = 400;
        const timeBetweenTransitions = 6000;
        let currentColourIndex = 0;
        setInterval(function() {
            if (currentColourIndex >= coloursInOrder.length) {
                currentColourIndex = 0;
            }
            let nextColour = currentColourIndex + 1;
            if (nextColour >= coloursInOrder.length) {
                nextColour = 0;
            }
            fade(heroTitle, coloursInOrder[currentColourIndex], coloursInOrder[nextColour], transitionDuration);
        }, timeBetweenTransitions);
    }
});