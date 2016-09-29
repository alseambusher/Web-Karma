/*jshint esversion: 6 */
var open = require('open');
var path = require("path");
import env from './env';
import jetpack from 'fs-jetpack';

exports.links = {
  license: "https://github.com/usc-isi-i2/Web-Karma/blob/master/LICENSE.txt",
  documentation: "https://github.com/usc-isi-i2/Web-Karma/wiki",
  issues: "https://github.com/usc-isi-i2/Web-Karma/issues",
  about_karma: "https://github.com/usc-isi-i2/Web-Karma/blob/master/README.md",
  about_isi: "http://www.isi.edu/about/"
};

exports.tomcat = {
  path : __dirname + path.sep + "tomcat",
  launchURL : "http://localhost:8080"
};

exports.tomcat.logFile = exports.tomcat.path + path.sep + "logs" + path.sep + "karma.out";
exports.tomcat.catalina_home = exports.tomcat.path + path.sep + "bin";
exports.tomcat.catalina = exports.tomcat.catalina_home + path.sep + "catalina" + ((/^win/.test(process.platform)) ? ".bat" : ".sh");
// For windows run jpda start and for others run "run". This makes windows a bit slower but we can't help it as we need the log output
exports.tomcat.startcmd = exports.tomcat.catalina + ((/^win/.test(process.platform)) ? " jpda start" : " run 1> " + exports.tomcat.logFile + " 2<&1");
exports.tomcat.stopcmd = exports.tomcat.catalina + " stop";

process.env.CATALINA_HOME = exports.tomcat.path;
process.env.CATALINA_BASE = exports.tomcat.path;
process.env.CLASSPATH = exports.tomcat.catalina_home + path.sep + "bootstrap.jar;" + exports.tomcat.catalina_home + path.sep + "tomcat-juli.jar";

exports.start = function(){
  exports.getMinHeap((_min) => {
    exports.getMaxHeap((_max) => {
      var exec = require('child_process').exec;
      let options = {
        cwd: exports.tomcat.catalina_home
      };
      process.env.CATALINA_OPTS = "-Xms" + _min + "M -Xmx" + _max +"M";
      exec(exports.tomcat.startcmd, options, function(error, stdout, stderr) {
        console.log(error);
        console.log(stdout);
        console.log(stderr);
      });
    });
  });
};

exports.launch = function(){
  var spawn = require('child_process').spawn;
  open(exports.tomcat.launchURL);
};

exports.restart = function(){
  stop();
  // wait for 5 seconds and try to start
  setTimeout(exports.start, 5000);
};

export function stop(){
  var exec = require('child_process').exec;
  let options = {
    cwd: exports.tomcat.catalina_home
  };
  exec(exports.tomcat.stopcmd, options, function(error, stdout, stderr) {
    // TODO log if there is some problem
    console.log(error);
    console.log(stdout);
    console.log(stderr);
  });
}
exports.stop = stop;

exports.setMinHeap = function(value){
    var env = jetpack.cwd(__dirname).read('env.json', 'json');
    env.args["-Xms"] = value;
    jetpack.cwd(__dirname).write('env.json', env);
};

exports.getMinHeap = function(callback){
    var env = jetpack.cwd(__dirname).read('env.json', 'json');
    callback(env.args["-Xms"]);
};

exports.setMaxHeap = function(value){
    var env = jetpack.cwd(__dirname).read('env.json', 'json');
    env.args["-Xmx"] = value;
    jetpack.cwd(__dirname).write('env.json', env);
};

exports.getMaxHeap = function(callback){
    var env = jetpack.cwd(__dirname).read('env.json', 'json');
    callback(env.args["-Xmx"]);
};
