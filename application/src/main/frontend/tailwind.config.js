/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["../resources/templates/**/*.{html,js}"], // it will be explained later
    theme: {
        extend: {
            height: {
                'routine': '32rem'
            },
            backgroundImage: {
                'up-arrows-sparse': "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='150' height='150' viewBox='0 0 150 150'%3E%3Cg fill-rule='evenodd'%3E%3Cg fill='%23d4cfcb' fill-opacity='0.2'%3E%3Cpath d='M8 6L14 18h-12zm75 75l6 12h-12z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E\");",
                'texture': "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='4' height='4' viewBox='0 0 4 4'%3E%3Cpath fill='%239C92AC' fill-opacity='0.4' d='M1 3h1v1H1V3zm2-2h1v1H3V1z'%3E%3C/path%3E%3C/svg%3E\");",
                'texture-faded': "url(\"data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='4' height='4' viewBox='0 0 4 4'%3E%3Cpath fill='%23d9edce' fill-opacity='0.4' d='M1 3h1v1H1V3zm2-2h1v1H3V1z'%3E%3C/path%3E%3C/svg%3E\");",
            },
            fontFamily: {
                "sigmar": ["Sigmar", "serif"],
                "poppins": ["Poppins", "serif"],
                "ubuntu": ["Ubuntu", "serif"],
                "shrikhand": ["Shrikhand", "serif"],
            },
            dropShadow: {
                "crisp": "0 1.2px 1.2px rgba(0,0,0,0.8)"
            },
            colors: {
                "brown": "#855e0c"
            }
        }
    },
    plugins: [],
}