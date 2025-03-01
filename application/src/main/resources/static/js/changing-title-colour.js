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
        const lerp = function(a,b,u) {
            return (1-u) * a + u * b;
        };
        const fade = function(element, start, end, duration) {
            let interval = 10;
            let steps = duration/interval;
            let step_u = 1.0/steps;
            let u = 0.0;
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
        const scheduleChangeRedToYellow = function() {
            setTimeout(function(){
                fade(heroTitle,redRgb, yellowRgb,transitionDuration);
                scheduleChangeYellowToGreen();
            },timeBetweenTransitions);
        }
        const scheduleChangeYellowToGreen = function() {
            setTimeout(function(){
                fade(heroTitle,yellowRgb,greenRgb,transitionDuration);
                scheduleChangeGreenToBrown();
            },timeBetweenTransitions);
        }
        const scheduleChangeGreenToBrown = function() {
            setTimeout(function(){
                fade(heroTitle,greenRgb, brownRgb,transitionDuration);
                scheduleChangeBrownToBlue();
            },timeBetweenTransitions);
        }
        const scheduleChangeBrownToBlue = function() {
            setTimeout(function(){
                fade(heroTitle,brownRgb, blueRgb,transitionDuration);
                scheduleChangeBlueToPink();
            },timeBetweenTransitions);
        }
        const scheduleChangeBlueToPink = function() {
            setTimeout(function(){
                fade(heroTitle,blueRgb, pinkRgb,transitionDuration);
                scheduleChangePinkToBlack();
            },timeBetweenTransitions);
        }
        const scheduleChangePinkToBlack = function() {
            setTimeout(function(){
                fade(heroTitle,pinkRgb, blackRgb,transitionDuration);
                scheduleChangeBlackToRed();
            },timeBetweenTransitions);
        }
        const scheduleChangeBlackToRed = function() {
            setTimeout(function(){
                fade(heroTitle,blackRgb, redRgb,transitionDuration);
                scheduleChangeRedToYellow();
            },timeBetweenTransitions);
        }
        // Now schedule the first transition (the others will run recursively)
        scheduleChangeRedToYellow();
    }
});