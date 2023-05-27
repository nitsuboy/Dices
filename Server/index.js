const { Server } = require('http');
const { Socket } = require('socket.io');

var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

let players = []

async function upPlayerList(socket) {
    players = await io.allSockets()
    console.log(players)
    socket.emit('players',{ players: Array.from(players)})
}

io.local.disconnectSockets();

server.listen(8080, function(){
    console.log("Server started")
});

io.on('connection', function(socket){
    console.log('Player Connected');
    upPlayerList(socket);
    
    socket.emit('info',{ id: socket.id})
    socket.broadcast.emit('playerConnected', { player: socket.id})
    
    socket.on('msg', function(data){
        data.id = socket.id
        socket.broadcast.emit('newMessage', { sender: socket.id, msg: data.msg})
    })
    socket.on('disconnect', function(){
        socket.broadcast.emit('playerDiconnected', { player: socket.id})
        upPlayerList(socket);
        console.log('Player Disconnected');
    })
});