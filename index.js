// generateLibsJson.js
const fs = require('fs');
const crypto = require('crypto');
const path = require('path');

const baseDownloadUrl = 'https://raw.githubusercontent.com/hyzco/memory-bot/refs/heads/main/';
const dirs = ['lib', 'plugins']; // directories to scan
const outputFile = 'libs.json';

function sha256Base64(filePath) {
  const data = fs.readFileSync(filePath);
  return crypto.createHash('sha256').update(data).digest('base64');
}

function scanDir(dir) {
  if (!fs.existsSync(dir)) return [];
  return fs.readdirSync(dir)
    .filter(name => fs.statSync(path.join(dir, name)).isFile())
    .map(name => ({ dir, name }));
}

function generateJson() {
  const entries = {};

  for (const { dir, name } of dirs.flatMap(scanDir)) {
    const filePath = path.join(dir, name);
    const relPath = path.posix.join(dir, name);
    const sha256 = sha256Base64(filePath);

    const entry = {
      path: relPath,
      sha256,
      download: baseDownloadUrl + encodeURIComponent(name),
    };

    // optional: auto = true for .jar files (as in your example)
    if (name.endsWith('.jar')) entry.auto = true;

    entries[name] = entry;
  }

  fs.writeFileSync(outputFile, JSON.stringify(entries, null, 2) + '\n');
  console.log(`✅ ${outputFile} generated with ${Object.keys(entries).length} entries.`);
}

generateJson();
