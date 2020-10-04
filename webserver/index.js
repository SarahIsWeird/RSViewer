var express = require('express')
var app = express();

var contents = "";
var bufferedContents = "";

app.get('/', function(req, res) {
    console.log("get request");

    res.send(bufferedContents);
});

app.post('/', function(req, res) {
    if (req.get('update') != undefined) {
        contents = ""
    } else if (req.get('content') != undefined) {
        contents = contents + req.get('content');
    } else if (req.get('update_done') != undefined) {
        bufferedContents = contents;
    }

    for (k in req.headers) {
        console.log(k + ': ' + req.headers[k]);
    }

    console.log();

    if (req.get('update_done') == undefined) {
        res.send(".");
    } else {
        res.send("parsed")
    }
});

app.listen(80);