db = db.getSiblingDB('snookerup');

db.routines.insertMany([
    {
        _id: ObjectId('6a2bd369ff3dd11ea4905c6d'),
        routineId: 'clearing-the-colours',
        title: 'Clearing the Colours',
        descriptionLines: [
            'Put all colours on their spots, then try to clear them in order, i.e. yellow, green, brown, blue, pink, black.'
        ],
        variationsLinesHarder: [
            'Limiting the number of cushions the white is allowed to hit.',
            'Looping through the routine, i.e. pot yellow to black, but on the black play for position on the yellow, then replace the balls and try to clear again. With this configuration, your score is the number of completed loops.'
        ],
        titleImage: '/images/routines/clearing-the-colours-ls.png',
        otherImages: [ '/images/routines/clearing-the-colours.png' ],
        tags: [ 'match-situations', 'positional-play' ],
        variations: { loop: true, cushionLimit: true }
    },
    {
        _id: ObjectId('6a2bd369ff3dd11ea4905c6e'),
        routineId: 'eight-reds-with-blue-and-black',
        title: 'Eight Reds with Blue and Black',
        descriptionLines: [
            'Place eight reds in the top half of the table (between blue and black), all in the open, as indicated in the diagram.',
            "Make a break, using only the blue and black as colours (all others aren't even on the table)."
        ],
        variationsLinesHarder: [ 'Limiting the number of cushions the white is allowed to hit.' ],
        titleImage: '/images/routines/8-reds-between-blue-and-black-ls.png',
        otherImages: [ '/images/routines/8-reds-between-blue-and-black.png' ],
        tags: [ 'break-building', 'positional-play' ],
        variations: { cushionLimit: true }
    },
    {
        _id: ObjectId('6a2bd369ff3dd11ea4905c6f'),
        routineId: 'five-reds-within-triangle',
        title: 'Five Reds Within Triangle',
        descriptionLines: [
            'A more advanced break building and positional routine, the aim here is simple: pot five reds with five blacks, but there are some difficult restrictions and the setup is key.',
            'The reds should be arranged in a "V" or triangle, but with the middle reds in each line slightly further out, to give the shape a "bulge"',
            'Throughout the routine, each red should be potted with the white within (or near enough) the triangle formed by the reds, to make required position more difficult, and to reduce the distance the white will travel.'
        ],
        titleImage: '/images/routines/5-reds-within-triangle-ls.png',
        otherImages: [ '/images/routines/5-reds-within-triangle.png' ],
        tags: [ 'break-building', 'positional-play' ],
        variations: {}
    },
    {
        _id: ObjectId('6a2bd369ff3dd11ea4905c70'),
        routineId: 'straight-pots-along-blue-spot',
        title: 'Straight Pots Along Middle',
        descriptionLines: [
            'Practise long potting and good cueing by placing a number of balls in the line of the middle pockets and blue spot, then on each shot line the white up with your hand so the shot is dead straight, then try to pot as many as you can'
        ],
        variationsLinesEasier: [
            'Starting with less balls to pot, such as 3, then working your way up to all 21 balls (15 reds and 6 colours)'
        ],
        variationsLinesHarder: [
            'Restricting every pot to the same type of ball striking, e.g. stun, screw, deep screw, top, etc.'
        ],
        titleImage: '/images/routines/straight-pots-along-blue-spot-ls.png',
        otherImages: [
            '/images/routines/straight-pots-along-blue-spot.png',
            '/images/routines/straight-pots-along-blue-spot-3.png'
        ],
        tags: [ 'straight-cueing', 'long-potting' ],
        scoreUnit: 'Pots',
        unit: 'balls',
        variations: {
            unitNumbers: { min: 3, max: 21 },
            ballStriking: [ 'Screw', 'Deep screw', 'Stun', 'Stun run through', 'Top' ]
        }
    },
    {
        _id: ObjectId('6a2bd369ff3dd11ea4905c71'),
        routineId: 'the-line-up',
        title: 'The Line Up',
        descriptionLines: [
            'Arrange all reds in a line up the middle of the table, in line with the blue, pink, and black spots.',
            'Pot the balls in order (i.e. red, colour, red, and so on), trying to make as high a break as possible.',
            'Placement of the reds is up to you. Typically they are evenly spaced, including one or two above the blue, but advanced players often like to bunch the reds as much around the black as possible.'
        ],
        variationsLinesEasier: [
            "For beginner and intermediate levels, don't use all 15 reds. Start with 3 reds, try to clear the table, and work your way up to the full 15."
        ],
        variationsLinesHarder: [
            'Once you can clear a certain number of reds and all the colours, try to loop through the routine, i.e. complete the routine, then set it up and try to complete it again, playing the white on the first red from where it finished after the last black on the previous routine.',
            "Add self-imposed positional restrictions, such as limiting the number of cushions you're allowed to hit, potting the reds in a specific order, or only allowing the white to be on one side of the table."
        ],
        titleImage: '/images/routines/the-line-up-normal-ls.png',
        otherImages: [
            '/images/routines/the-line-up-normal.png',
            '/images/routines/the-line-up-by-black.png',
            '/images/routines/the-line-up-seven-reds.png',
            '/images/routines/the-line-up-three-reds.png'
        ],
        tags: [ 'break-building', 'positional-play' ],
        variations: {
            loop: true,
            cushionLimit: true,
            unitNumbers: { min: 3, max: 15 },
            potInOrder: true,
            stayOnOneSideOfTable: true
        }
    },
    {
        _id: ObjectId('6a2bd369ff3dd11ea4905c72'),
        routineId: 'the-t-line-up',
        title: 'The T Line Up',
        descriptionLines: [
            'A variation on the traditional Line Up routine, the T Line Up is so called because rather than the reds being arranged in one line up the table, it instead resembles a "T" shape.',
            'Pot the balls in order (i.e. red, colour, red, and so on), trying to make as high a break as possible.',
            'The typical placement sees 5 reds in a line between pink and black, then five reds each in a line either side of the pink to the side cushion.',
            'Alternative placements of the reds include placing some below the black instead of on the "wings".'
        ],
        variationsLinesEasier: [
            "For beginner and intermediate levels, don't use all 15 reds. Start with 3 reds, try to clear the table, and work your way up to the full 15."
        ],
        variationsLinesHarder: [
            'Once you can clear a certain number of reds and all the colours, try to loop through the routine, i.e. complete the routine, then set it up and try to complete it again, playing the white on the first red from where it finished after the last black on the previous routine.',
            "Add self-imposed positional restrictions, such as limiting the number of cushions you're allowed to hit, potting the reds in a specific order, or only allowing the white to be on one side of the table."
        ],
        titleImage: '/images/routines/t-line-up-normal-ls.png',
        otherImages: [
            '/images/routines/t-line-up-normal.png',
            '/images/routines/t-line-up-more-around-black.png',
            '/images/routines/t-line-up-10-reds.png',
            '/images/routines/t-line-up-8-reds-spread.png'
        ],
        tags: [ 'break-building', 'positional-play' ],
        variations: {
            loop: true,
            cushionLimit: true,
            unitNumbers: { min: 3, max: 15 },
            potInOrder: true,
            stayOnOneSideOfTable: true
        }
    },
    {
        _id: ObjectId('6a2bd369ff3dd11ea4905c73'),
        routineId: 'the-x-line-up',
        title: 'The X Line Up',
        descriptionLines: [
            'A variation on the traditional Line Up routine, the X Line Up is so called because rather than the reds being arranged in one line up the table, it instead resembles an "X" shape.',
            'Pot the balls in order (i.e. red, colour, red, and so on), trying to make as high a break as possible.',
            'Put the reds in lines between the pink and all middle/black pockets, close to the pink. If you use all 15 reds, this should be one line of three reds, and three lines of four reds, the line with the odd red is up to you.',
            'Alternative placements of the reds include placing some below the black instead of on the "wings".'
        ],
        variationsLinesEasier: [
            "For beginner and intermediate levels, don't use all 15 reds. Start with 4 reds, try to clear the table, and work your way up to the full 15."
        ],
        variationsLinesHarder: [
            'Once you can clear a certain number of reds and all the colours, try to loop through the routine, i.e. complete the routine, then set it up and try to complete it again, playing the white on the first red from where it finished after the last black on the previous routine.',
            "Add self-imposed positional restrictions, such as limiting the number of cushions you're allowed to hit, or potting the reds in a specific order."
        ],
        titleImage: '/images/routines/x-line-up-normal-ls.png',
        otherImages: [
            '/images/routines/x-line-up-normal.png',
            '/images/routines/x-line-up-8-reds.png'
        ],
        tags: [ 'break-building', 'positional-play' ],
        variations: {
            loop: true,
            cushionLimit: true,
            unitNumbers: { min: 4, max: 15 },
            potInOrder: true
        }
    },
    {
        _id: ObjectId('6a2bd369ff3dd11ea4905c74'),
        routineId: 'the-y-line-up',
        title: 'The Y Line Up',
        descriptionLines: [
            'A variation on the traditional Line Up routine, the Y Line Up is so called because rather than the reds being arranged in one line up the table, it instead resembles a "Y" shape.',
            'Pot the balls in order (i.e. red, colour, red, and so on), trying to make as high a break as possible.',
            'The typical placement sees 5 reds in a line between pink and black, then five reds each in a line either side of the pink to the middle pocket.',
            'Alternative placements of the reds include placing some below the black instead of on the "wings".',
            'This is similar to the T Line Up, but slightly more difficult in that the pink is typically out of commission, so you need to focus more on blacks.'
        ],
        variationsLinesEasier: [
            "For beginner and intermediate levels, don't use all 15 reds. Start with 3 reds, try to clear the table, and work your way up to the full 15."
        ],
        variationsLinesHarder: [
            'Once you can clear a certain number of reds and all the colours, try to loop through the routine, i.e. complete the routine, then set it up and try to complete it again, playing the white on the first red from where it finished after the last black on the previous routine.',
            "Add self-imposed positional restrictions, such as limiting the number of cushions you're allowed to hit, or potting the reds in a specific order."
        ],
        titleImage: '/images/routines/y-line-up-normal-ls.png',
        otherImages: [
            '/images/routines/y-line-up-normal.png',
            '/images/routines/y-line-up-more-around-black.png',
            '/images/routines/y-line-up-10-reds.png'
        ],
        tags: [ 'break-building', 'positional-play' ],
        variations: {
            loop: true,
            cushionLimit: true,
            unitNumbers: { min: 3, max: 15 },
            potInOrder: true
        }
    },
    {
        _id: ObjectId('6a2bd369ff3dd11ea4905c75'),
        routineId: 'three-distant-reds-and-colours',
        title: 'Three Distant Reds and Colours',
        descriptionLines: [
            "This routine practises a common scenario seen in frames. It's been nip and tuck, then all of a sudden you get an opportunity where, with a few reds remaining, they're all pottable, but far apart on the table.",
            'For the setup of this routine, place one red directly between pink and black, another directly between blue and pink, and one more directly between brown and blue. Start by placing the white wherever you want, then pot all three reds with colours and clear the table.',
            'The key to this routine is picking the best route through the table, then achieving good judgement of pace to get good position each time.'
        ],
        titleImage: '/images/routines/3-distant-reds-and-colours-ls.png',
        otherImages: [ '/images/routines/3-distant-reds-and-colours.png' ],
        tags: [ 'break-building', 'positional-play', 'match-situations' ],
        variations: {}
    }
]);