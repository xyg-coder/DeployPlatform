// vue.config.js
module.exports = {
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:8088',
                ws: true,  // true if the websocket will be proxy also
                changeOrigin: true
            }
        }
    },
    outputDir: 'target/dist',
    assetsDir: 'static',
}