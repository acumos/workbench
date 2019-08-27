/*
===============LICENSE_START=======================================================
Acumos Apache-2.0
===================================================================================
Copyright (C) 2019 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
===================================================================================
This Acumos software file is distributed by AT&T and Tech Mahindra
under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
     http://www.apache.org/licenses/LICENSE-2.0
 
This file is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
===============LICENSE_END=========================================================
*/

var express = require("express");
var https = require("https");
var bodyParser = require('body-parser');
var cookieParser = require('cookie-parser');
var methodOverride = require('method-override');
var cors = require('cors')

var app = express();
var port = process.env.PORT || 9092;

app.use(cors());

app.use(express.static("../component/build/default"));
app.use(cookieParser());
app.use(methodOverride());

app.use(bodyParser.json());

app.use(bodyParser.raw());

app.use(bodyParser.urlencoded({
	extended : true,
}));

app.use(bodyParser.text());

require('./routes_services.js')(app);


var server = app.listen(port, function() {
	console.info('running on ...'+ port);
});

server.timeout = process.env.timeout || 840000; 
