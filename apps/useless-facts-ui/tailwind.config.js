const { createGlobPatternsForDependencies } = require('@nx/angular/tailwind');
const { join } = require('path');

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    join(__dirname, 'src/**/!(*.stories|*.spec).{ts,html}'),
    ...createGlobPatternsForDependencies(__dirname),
  ],
  theme: {
    extend: {
      keyframes: {
        dissolve: {
          '0%': { opacity: 0 },
          '100%': { opacity: '1' },
        }
      },
      animation: {
        dissolve: 'dissolve 2s ease-in forwards',
      },
      colors: {
        primary: '#0018A8',
      },
    },
  },
  plugins: [
    require('@tailwindcss/line-clamp')
  ],
};
