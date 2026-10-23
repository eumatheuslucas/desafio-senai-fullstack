/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Geist', 'ui-sans-serif', 'system-ui', '-apple-system', 'Segoe UI', 'sans-serif'],
      },
      colors: {
        primary: { DEFAULT: '#155DFC', hover: '#1447E6', soft: '#EFF6FF', softText: '#1447E6' },
        ink: '#09090B',
        muted: { DEFAULT: '#F4F4F5', subtle: '#FAFAFA', fg: '#686872' },
        line: '#E4E4E7',
        success: { DEFAULT: '#00813A', soft: '#F0FDF4', border: '#B9F8CF', text: '#016630' },
        warning: { DEFAULT: '#955200', soft: '#FFFBEB', border: '#FEE685', text: '#953D00' },
        danger: { DEFAULT: '#E40016', soft: '#FEF2F2', border: '#FFCACA', text: '#9F000F' },
      },
    },
  },
  plugins: [],
};
