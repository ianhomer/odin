testName=${1:-"test.js"}
echo "Debugging tests : ${testName}"
node --inspect-brk node_modules/.bin/jest --runInBand ${testName}