const fs = require('fs');
const crypto = require('crypto');
const path = require('path');

const baseDownloadUrl = 'https://raw.githubusercontent.com/hyzco/memory-bot/refs/heads/main/file/';
const directories = ['./']; // directories to scan
const outputFile = 'libs.json';

function sha256Base64(filePath) {
  const data = fs.readFileSync(filePath);
  return crypto.createHash('sha256').update(data).digest('base64');
}

function scanAndGenerate() {
  const result = {};

  for (const dir of directories) {
    if (!fs.existsSync(dir)) continue;
    const files = fs.readdirSync(dir).filter(f => fs.statSync(path.join(dir, f)).isFile());

    for (const file of files) {
      const fullPath = path.join(dir, file);
      const sha256 = sha256Base64(fullPath);
      const relPath = path.join(dir, file).replace(/\\/g, '/'); // normalize slashes for Windows

      result[file] = {
        path: relPath,
        sha256: sha256,
        download: baseDownloadUrl + encodeURIComponent(file)
      };

      // Example: add "auto": true for jars in plugins, you can customize this
      if (dir === 'plugins' && file.endsWith('.jar')) {
        result[file].auto = true;
      }
    }
  }

  return result;
}

const libsJson = scanAndGenerate();
fs.writeFileSync(outputFile, JSON.stringify(libsJson, null, 2));
console.log(`Generated ${outputFile} with SHA-256 base64 hashes.`);
