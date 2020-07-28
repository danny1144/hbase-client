var fs = require('fs');
var path = require('path');
var someFile = path.resolve(__dirname + '/dist/index.html');
fs.readFile(someFile, 'utf8', function(err, data) {
  if (err) {
    return console.log(err);
  }
  var result = data
    .replace(/link href=\/css/g, 'link href=/hbase-client/css')
    .replace(/link href=\/js/g, 'link href=/hbase-client/js')
    .replace(/script src=\/js/g, 'script src=/hbase-client/js');

  fs.writeFile(someFile, result, 'utf8', function(err) {
    if (err) return console.log(err);
  });
});
