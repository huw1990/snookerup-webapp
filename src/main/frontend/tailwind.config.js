/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["../resources/templates/**/*.{html,js}"], // it will be explained later
    theme: {
        extend: {
            height: {
                'routine': '30rem'
            }
        },
    },
    plugins: [],
}