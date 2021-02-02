const request = require('request');
const cheerio = require('cheerio');
const encry = require('./encry');
const fetch = require('node-fetch');
var options1 = {
    'method': 'GET',
    'url': 'https://newids.seu.edu.cn/authserver/login?goto=https://seicwxbz.seu.edu.cn/cas-we-can/cas-login-callback/c625f57c-fa93-4c37-af72-16a52a065b49',
    'headers': {
        'sec-ch-ua': '"Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87"',
        'sec-ch-ua-mobile': '?0',
        'Upgrade-Insecure-Requests': '1',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36',
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
        'Sec-Fetch-Site': 'none',
        'Sec-Fetch-Mode': 'navigate',
        'Sec-Fetch-User': '?1',
        'Sec-Fetch-Dest': 'document',
        'Cookie': 'route=249cb391d30fa3e46a1009b55ebd85fc; JSESSIONID=E0NcxlMBkANDmhH7BkvmM40u8plSNt6BSbK77Tj1pioajz3WQDJ9!-543703607'
    }
};

request(options1, function (error, response) {
    if (error) throw new Error(error);
    const ch = cheerio.load(response.body);
    var password = encry._etd2("zxd960211",ch('#pwdDefaultEncryptSalt').val());
    var lt = ch("input[name=lt]").val();
    console.log(ch('#pwdDefaultEncryptSalt').val());
    var options2 = {
        'method': 'POST',
            'url': 'https://newids.seu.edu.cn/authserver/login?goto=https://seicwxbz.seu.edu.cn/cas-we-can/cas-login-callback/c625f57c-fa93-4c37-af72-16a52a065b49',
        'headers': {
            'sec-ch-ua': '"Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87"',
            'sec-ch-ua-mobile': '?0',
            'Upgrade-Insecure-Requests': '1',
            'Content-Type': 'application/x-www-form-urlencoded',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9',
            'Sec-Fetch-Site': 'same-origin',
            'Sec-Fetch-Mode': 'navigate',
            'Sec-Fetch-User': '?1',
            'Sec-Fetch-Dest': 'document',
            'Cookie': 'route=249cb391d30fa3e46a1009b55ebd85fc; JSESSIONID=E0NcxlMBkANDmhH7BkvmM40u8plSNt6BSbK77Tj1pioajz3WQDJ9!-543703607'
        },
        form: {
            '_eventId': 'submit',
            'dllt': 'userNamePasswordLogin',
            'execution': 'e1s1',
            'lt': lt,
            'password': password,
            'rmShown': '1',
            'username': '220194882'
        }
    };
    request(options2, function (error, response) {
        console.log(response.body);
        var location = response.headers.location;
        console.log('location: ' + location);
    });

/*    fetch("https://newids.seu.edu.cn/authserver/login?goto=https://seicwxbz.seu.edu.cn/cas-we-can/cas-login-callback/c625f57c-fa93-4c37-af72-16a52a065b49", {
        headers: {
            'sec-ch-ua': '"Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87"',
            'sec-ch-ua-mobile': '?0',
            'Upgrade-Insecure-Requests': '1',
            'Content-Type': 'application/x-www-form-urlencoded',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36',
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*!/!*;q=0.8,application/signed-exchange;v=b3;q=0.9',
            'Sec-Fetch-Site': 'same-origin',
            'Sec-Fetch-Mode': 'navigate',
            'Sec-Fetch-User': '?1',
            'Sec-Fetch-Dest': 'document',
            'Cookie': 'route=249cb391d30fa3e46a1009b55ebd85fc; JSESSIONID=E0NcxlMBkANDmhH7BkvmM40u8plSNt6BSbK77Tj1pioajz3WQDJ9!-543703607'
        },
        method: 'POST',
        body: {
            '_eventId': 'submit',
            'dllt': 'userNamePasswordLogin',
            'execution': 'e1s1',
            'lt': lt,
            'password': password,
            'rmShown': '1',
            'username': '220194882'
        }
    }).then(function (res){
        console.log(res.headers)
    });*/
});


