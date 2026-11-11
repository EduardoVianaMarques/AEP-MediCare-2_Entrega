// Servidor apenas para testes: não substitui o backend Spring Boot.
const http = require('node:http');
const fs = require('node:fs');
const path = require('node:path');
const root = path.resolve(__dirname,'../src/main/resources/static');
http.createServer((req,res) => {
    const pathname = new URL(req.url,'http://localhost').pathname;
    const file = path.resolve(root,'.' + ({'/':'/index.html','/medicamentos':'/medicamentos.html','/login':'/login.html','/cadastro':'/cadastro.html'}[pathname] || pathname));
    if (!file.startsWith(root + path.sep)) {res.writeHead(403);return res.end();}
    fs.readFile(file,(err,body) => {
        if(err) {res.writeHead(404);return res.end();}
        res.setHeader('Content-Type',({'.html':'text/html; charset=utf-8','.css':'text/css','.js':'text/javascript','.svg':'image/svg+xml'})[path.extname(file)] || 'text/plain');
        res.end(body);
    });
}).listen(4173,'127.0.0.1');
