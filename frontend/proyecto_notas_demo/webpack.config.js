const path = require('path');

module.exports = {
  entry: './src/main.ts',   // archivo principal TS
  module: {
    rules: [
      {
        test: /\.ts$/, // Se analizan todos los ficheros que terminan en .ts
        use: 'ts-loader', // Usando el type script loader
        include: [path.resolve(__dirname, "src")] // Y haremos eso en el directorio src
        //exclude: /node_modules/,
      },
    ],
  },
  resolve: {
    extensions: ['.ts', '.js'], // reconoce TS y JS
  },
  output: {
    filename: 'bundle.js',      // archivo de salida
    path: path.resolve(__dirname, 'public/js/'), // carpeta de salida
  },
  mode: 'development'          // desarrollo (usa 'production' para producción)
};