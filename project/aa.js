var jsdom = require("jsdom");
const { JSDOM } = jsdom;
const { window } = new JSDOM();
const { document } = (new JSDOM('')).window;
global.document = document;

var $ = jQuery = require('jquery')(window);

$.ajax({
    url:'https://newids.seu.edu.cn/authserver/login?goto=https://seicwxbz.seu.edu.cn/cas-we-can/cas-login-callback/e3c3a576-a3b1-49e7-a840-8a19813eb4fe',
    method: 'get',
    headers: {
        'sec-ch-ua': '"Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87"',
        'sec-ch-ua-mobile': '?0',
        'Upgrade-Insecure-Requests': '1',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
        'Sec-Fetch-Site': 'none',
        'Sec-Fetch-Mode': 'navigate',
        'Sec-Fetch-User': '?1',
        'Sec-Fetch-Dest': 'document',
        'Access-Control-Allow-Origin':'*',
        'Cookie': 'route=249cb391d30fa3e46a1009b55ebd85fc; JSESSIONID=E0NcxlMBkANDmhH7BkvmM40u8plSNt6BSbK77Tj1pioajz3WQDJ9!-543703607'
    },
    success:(function (response){
        console.log('success');
        console.log(response);
    }),
    error:(function (response){
        console.log(response)
    })
})