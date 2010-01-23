function MsgQueue(div,host,port) {
  this.flashDiv = div;
  this.host = host;
  this.port = port;
  this.queue = [];
  this.ready = true; //Set to false if in a async process.
}

MsgQueue.prototype.connectToServer = function() {
  var flash = document.getElementById(this.flashDiv);
  flash.openMindsEye(this.host,this.port); //openMindsEye() is the first argument in the flash object's ExternalInterface.addCallback
}

MsgQueue.prototype.messagePush = function(msg) {
  var flash = document.getElementById(this.flashDiv);
  flash.messagePush(msg); //messagePush() is the first argument in the flash object's ExternalInterface.addCallback
}

//Call this after every msg is done processing.
MsgQueue.prototype.next = function() {
  if (this.queue.length < 1) {this.ready = true;return false;}
  var msg = this.queue.shift();
  return this.process(msg);
}

MsgQueue.prototype.add = function(msg) {
  this.queue.push(msg);
  if (this.ready) this.next();
}

//Tailor this anyway you want.
MsgQueue.prototype.process = function(msg) {
  //Process MSG.
  switch (msg.messageType) {
    case "SAY": writeToConsole(msg.jsonStr);this.next();break;
    default: break;
  }
}
